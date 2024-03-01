package com.example.gotogetherbe.chat.repository;

import com.example.gotogetherbe.chat.entity.ChatMember;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {
  Optional<ChatMember> findByChatRoomIdAndMemberId(Long chatRoomId, Long memberId);

  List<ChatMember> findAllByChatRoomId(Long chatRoomId);

  List<ChatMember> findAllByMemberId(Long memberId);

}
