package com.example.gotogetherbe.global.component;

import com.example.gotogetherbe.notification.dto.NotificationInfoDto;
import com.example.gotogetherbe.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {

    private final NotificationService notificationService;

    @Async // 비동기 처리
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 트랜잭션 분리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // 트랜잭션 commit 이후에 실행
    public void sendNotification(NotificationInfoDto notificationInfo) {
        notificationService.send(notificationInfo);
    }

}
