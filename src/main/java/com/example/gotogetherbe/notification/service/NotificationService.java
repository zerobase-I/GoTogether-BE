package com.example.gotogetherbe.notification.service;

import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.notification.repository.EmitterRepository;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final MemberRepository memberRepository;
    private final EmitterRepository emitterRepository;
    private static final long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    /**
     * 서버 구독(SSE connection)
     *
     * @param email       로그인 사용자 email
     * @param lastEventId 마지막으로 수신한 이벤트 아이디
     * @return 연결된 SseEmitter
     */
    public SseEmitter subscribe(String email, String lastEventId) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        // lastEventId가 nulld이 아닐때 데이터가 유실된 시점 파악을 위해 current time을 이용하여 emitterId 생성
        String emitterId = member.getId() + "_" + System.currentTimeMillis();
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        // emitter delete
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onError((e) -> emitterRepository.deleteById(emitterId));

        // 503 error 방지 더미데이터 전송
        sendToClient(emitter, emitterId, "EventStream Created. [memberId=" + member.getId() + "]");

        // lastEventId가 존재할 경우 해당 이벤트부터 전송
        if (Objects.nonNull(lastEventId) && !lastEventId.isEmpty()) {
            Map<String, Object> eventCache = emitterRepository
                .findAllEventCacheStartWithByMemberId(member.getId() + "_");
            eventCache.entrySet().stream()
                .filter(
                    entry -> lastEventId.compareTo(entry.getKey()) < 0) // lastEventId 이후 데이터만 전송
                .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
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
        } catch (IOException e) {
            emitterRepository.deleteById(emitterId);
            emitter.completeWithError(e);
        }
    }

}
