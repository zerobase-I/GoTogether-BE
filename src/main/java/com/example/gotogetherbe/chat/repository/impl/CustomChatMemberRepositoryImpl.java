package com.example.gotogetherbe.chat.repository.impl;

import com.example.gotogetherbe.chat.entity.QChatMember;
import com.example.gotogetherbe.chat.repository.CustomChatMemberRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomChatMemberRepositoryImpl implements CustomChatMemberRepository {

    private final JPAQueryFactory jpa;

    @Override
    public boolean isUsersInSameChatRoom(Long reviewerId, Long targetMemberId, Long chatRoomId) {
        QChatMember chatMember = QChatMember.chatMember;
        BooleanExpression condition = chatMember.chatRoom.id.eq(chatRoomId)
            .and(chatMember.member.id.in(reviewerId, targetMemberId));

        Long count = jpa.select(chatMember.count())
            .from(chatMember)
            .where(condition)
            .fetchOne();

        return count != null && count == 2L;
    }
}
