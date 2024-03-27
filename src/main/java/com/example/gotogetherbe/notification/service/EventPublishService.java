package com.example.gotogetherbe.notification.service;

import static com.example.gotogetherbe.notification.type.NotificationStatus.UNREAD;

import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.notification.dto.NotificationInfoDto;
import com.example.gotogetherbe.notification.type.NotificationType;
import com.example.gotogetherbe.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublishService {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(Post post, Member member, NotificationType type) {
        NotificationInfoDto event = NotificationInfoDto.builder()
            .member(member)
            .postId(post.getId())
            .postTitle(post.getTitle())
            .status(UNREAD)
            .type(type)
            .build();

        applicationEventPublisher.publishEvent(event);
    }
}
