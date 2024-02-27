package com.example.gotogetherbe.chatmember.repository;

import com.example.gotogetherbe.chatmember.entity.ChatMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
  Optional<ChatMember> findByChatRoomIdAndMemberId(Long chatRoomId, Long memberId);
}
