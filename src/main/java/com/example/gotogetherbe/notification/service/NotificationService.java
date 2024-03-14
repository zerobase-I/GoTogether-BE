package com.example.gotogetherbe.notification.service;

import static com.example.gotogetherbe.global.exception.type.ErrorCode.NOTIFICATION_NOT_FOUND;
import static com.example.gotogetherbe.notification.type.NotificationStatus.*;

import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.notification.dto.NotificationDto;
import com.example.gotogetherbe.notification.dto.NotificationInfoDto;
import com.example.gotogetherbe.notification.entity.Notification;
import com.example.gotogetherbe.notification.repository.EmitterRepository;
import com.example.gotogetherbe.notification.repository.NotificationRepository;
import com.example.gotogetherbe.notification.type.NotificationStatus;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private static final long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    /**
     * 서버 구독(SSE connection)
     *
     * @param email       로그인 사용자 email
     * @param lastEventId 마지막으로 수신한 이벤트 아이디
     * @return 연결된 SseEmitter
     */
    public SseEmitter subscribe(String email, String lastEventId) {
        // lastEventId가 nulld이 아닐때 데이터가 유실된 시점 파악을 위해 current time을 이용하여 emitterId 생성
        String emitterId = email + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        // emitter delete
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onError((e) -> emitterRepository.deleteById(emitterId));

        // 503 error 방지 더미데이터 전송
        sendToClient(emitter, emitterId, "EventStream Created. [userEmail=" + email + "]");

        // lastEventId가 존재할 경우 해당 이벤트부터 전송
        if (Objects.nonNull(lastEventId) && !lastEventId.isEmpty()) {
            sendLostData(email, lastEventId, emitter);
        }

        return emitter;
    }

    /**
     * 알림 전송
     *
     * @param notificationInfo 알림 정보
     */
    public void send(NotificationInfoDto notificationInfo) {
        Notification notification = notificationRepository.save(
            notificationInfo.of()); //repository에 저장
        log.info("알림 저장 완료");

        Map<String, SseEmitter> emitters = emitterRepository
            .findAllEmitterStartWithByEmail(
                notification.getMember().getEmail() + "_"); // 연결된 emitter 조회

        emitters.forEach((key, emitter) -> {
            emitterRepository.saveEventCache(key,
                notification.getId()); // event cache 저장(notification id만 저장)
            sendToClient(emitter, key, "New Notification");
        });
    }

    /**
     * 알림 조회
     * @param email 사용자 email
     * @return 알림dto 리스트
     */
    public List<NotificationDto> getNotifications(String email) {
        List<Notification> notifications =
            notificationRepository.findAllByMember_EmailOrderByCreatedAtDesc(email);

        return notifications.stream()
            .map(NotificationDto::from)
            .toList();
    }

    /**
     * 알림 확인, 상태 변경(unread -> read)
     * @param notificationId 알림 id
     * @return 알림 확인 시 이동할 url
     */
    @Transactional
    public String readNotification(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new GlobalException(NOTIFICATION_NOT_FOUND));

        notification.updateStatus(READ);
        notificationRepository.save(notification);

        return notification.getUrl();
    }

    /**
     * 클라이언트로 데이터 전송
     *
     * @param emitter   emitter
     * @param emitterId emitterId
     * @param data      data
     */
    private void sendToClient(SseEmitter emitter, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                .id(emitterId)
                .data(data));
            log.info("알림 전송 완료");
        } catch (IOException e) {
            emitterRepository.deleteById(emitterId);
            emitter.completeWithError(e);
            log.info("알림 전송 실패. userEmail: {}", emitterId.split("_")[0]);
        }
    }

    /**
     * 유실된 데이터 전송
     *
     * @param email       사용자 email
     * @param lastEventId 마지막으로 수신한 이벤트 아이디
     * @param emitter     emitter
     */
    private void sendLostData(String email, String lastEventId, SseEmitter emitter) {
        Map<String, Object> eventCache = emitterRepository
            .findAllEventCacheStartWithByEmail(email + "_");
        eventCache.entrySet().stream()
            .filter(
                entry -> lastEventId.compareTo(entry.getKey()) < 0) // lastEventId 이후 데이터만 전송
            .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
    }
}
