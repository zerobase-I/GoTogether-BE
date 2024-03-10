package com.example.gotogetherbe.chat.repository;

import com.example.gotogetherbe.chat.entity.ChatMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
  Optional<ChatMessage> findTopByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId);

  List<ChatMessage> findAllByChatRoomId(Long chatRoomId);
}
