package com.example.gotogetherbe.chatroom.repository;

import com.example.gotogetherbe.chatroom.entity.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
  Boolean existsByPostId(Long postId);

  List<ChatRoom> findAllByMemberId(Long memberId);
}
