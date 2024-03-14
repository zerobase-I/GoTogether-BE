package com.example.gotogetherbe.notification.controller;

import com.example.gotogetherbe.auth.config.LoginUser;
import com.example.gotogetherbe.notification.dto.NotificationDto;
import com.example.gotogetherbe.notification.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    private final NotificationService notificationService;

    // SSE 연결
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(
        @LoginUser String username,
        @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {

        return ResponseEntity.ok().body(notificationService.subscribe(username, lastEventId));
    }

    // 알림 조회
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications(@LoginUser String username) {
        return ResponseEntity.ok(notificationService.getNotifications(username));
    }

    // 알림 확인
    @GetMapping("/{notificationId}")
    public ResponseEntity<String> readNotification(@PathVariable Long notificationId) {
        notificationService.readNotification(notificationId);
        return ResponseEntity.ok(notificationService.readNotification(notificationId));
    }
}
