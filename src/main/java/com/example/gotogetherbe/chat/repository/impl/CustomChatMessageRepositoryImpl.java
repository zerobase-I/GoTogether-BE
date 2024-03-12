package com.example.gotogetherbe.chat.repository.impl;

import static com.example.gotogetherbe.chat.entity.QChatMember.chatMember;
import static com.example.gotogetherbe.chat.entity.QChatMessage.chatMessage;

import com.example.gotogetherbe.chat.entity.ChatMessage;
import com.example.gotogetherbe.chat.repository.CustomChatMessageRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.util.ObjectUtils;

@RequiredArgsConstructor
public class CustomChatMessageRepositoryImpl implements CustomChatMessageRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public Slice<ChatMessage> findChatRoomMessage(Long messageId, Long roomId, Pageable pageable) {
    List<ChatMessage> messages = jpaQueryFactory.selectFrom(chatMessage)
        .join(chatMessage.chatMember, chatMember)
        .fetchJoin()
        .where(
            lastMessageId(messageId),
            chatMessage.chatRoom.id.eq(roomId)
        )
        .limit(pageable.getPageSize()+1)
        .orderBy(chatMessage.createdAt.desc())
        .fetch();

    return new SliceImpl<>(messages, pageable, checkLastPage(messages, pageable));
  }

  private BooleanExpression lastMessageId(Long messageId) {
    return ObjectUtils.isEmpty(messageId) ? null : chatMessage.id.lt(messageId);
  }

  private boolean checkLastPage(List<ChatMessage> messages, Pageable pageable) {
    if (messages.size() > pageable.getPageSize()) {
      messages.remove(pageable.getPageSize());
      return true;
    }
    return false;
  }

}
