package com.example.gotogetherbe.notification.dto;

import com.example.gotogetherbe.notification.entity.Notification;
import com.example.gotogetherbe.notification.type.NotificationStatus;
import com.example.gotogetherbe.notification.type.NotificationType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDto {

    private Long id;

    private Long postId;

    private String postTitle;

    private Long memberId;

    private NotificationType type;

    private NotificationStatus status;

    private LocalDateTime createdAt;

    public static NotificationDto from(Notification notification) {
        return NotificationDto.builder()
            .id(notification.getId())
            .postId(notification.getPostId())
            .postTitle(notification.getPostTitle())
            .memberId(notification.getMember().getId())
            .type(notification.getType())
            .status(notification.getStatus())
            .createdAt(notification.getCreatedAt())
            .build();
    }

}
