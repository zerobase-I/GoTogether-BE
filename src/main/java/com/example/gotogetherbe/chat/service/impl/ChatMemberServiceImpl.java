package com.example.gotogetherbe.chat.service.impl;

import com.example.gotogetherbe.chat.dto.ChatMemberDto;
import com.example.gotogetherbe.chat.entity.ChatMember;
import com.example.gotogetherbe.chat.repository.ChatMemberRepository;
import com.example.gotogetherbe.chat.service.ChatMemberService;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMemberServiceImpl implements ChatMemberService {
  private final ChatMemberRepository chatMemberRepository;
  private final MemberRepository memberRepository;

  public List<ChatMemberDto> getChatMemberList(String email, Long chatRoomId) {
    Member member = memberRepository.findByEmail(email)
        .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

    List<ChatMember> chatMemberList = chatMemberRepository.findAllByChatRoomId(chatRoomId);

    if(chatMemberList.stream().noneMatch(chatMember -> Objects.equals(
        chatMember.getMember().getId(), member.getId()))) {
      throw new GlobalException(ErrorCode.NOT_BELONG_TO_CHAT_MEMBER);
    }
    if (chatMemberList.isEmpty()) {
      throw new GlobalException(ErrorCode.CHATROOM_IS_EMPTY);
    }

    return chatMemberList.stream().map(ChatMemberDto::from).collect(Collectors.toList());
  }
}
