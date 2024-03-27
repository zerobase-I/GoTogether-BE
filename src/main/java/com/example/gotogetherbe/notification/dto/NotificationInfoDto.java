package com.example.gotogetherbe.notification.dto;

import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.notification.entity.Notification;
import com.example.gotogetherbe.notification.type.NotificationStatus;
import com.example.gotogetherbe.notification.type.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationInfoDto {

    private Member member;

    private NotificationType type;

    private Long postId;

    private String postTitle;

    private NotificationStatus status;

    public Notification of() {
        return Notification.builder()
            .member(this.member)
            .type(this.type)
            .postId(this.postId)
            .postTitle(this.postTitle)
            .status(this.status)
            .build();
    }

}
