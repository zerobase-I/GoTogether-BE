package com.example.gotogetherbe.notification.service;

import static com.example.gotogetherbe.notification.type.NotificationStatus.UNREAD;

import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.notification.dto.NotificationInfoDto;
import com.example.gotogetherbe.notification.type.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublishService {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(Long postId, Member member, NotificationType type) {
        NotificationInfoDto event = NotificationInfoDto.builder()
            .member(member)
            .postId(postId)
            .status(UNREAD)
            .type(type)
            .build();

        applicationEventPublisher.publishEvent(event);
    }
}
