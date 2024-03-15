package com.example.gotogetherbe.chat.repository;

import com.example.gotogetherbe.chat.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomChatMessageRepository {
  Slice<ChatMessage> findChatRoomMessage(Long messageId, Long roomId, Pageable pageable);
}
