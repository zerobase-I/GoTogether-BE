package com.example.gotogetherbe.chat.repository;

import com.example.gotogetherbe.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
  Boolean existsByPostId(Long postId);
}
