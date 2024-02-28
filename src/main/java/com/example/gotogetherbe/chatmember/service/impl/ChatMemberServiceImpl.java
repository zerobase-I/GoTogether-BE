package com.example.gotogetherbe.chatmember.service.impl;

import com.example.gotogetherbe.chatmember.dto.ChatMemberDto;
import com.example.gotogetherbe.chatmember.entity.ChatMember;
import com.example.gotogetherbe.chatmember.repository.ChatMemberRepository;
import com.example.gotogetherbe.chatmember.service.ChatMemberService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMemberServiceImpl implements ChatMemberService {
  private final ChatMemberRepository chatMemberRepository;
  private final MemberRepository memberRepository;

  public List<ChatMemberDto> getChatMemberList(Long chatRoomId) {
    List<ChatMember> chatMemberList = chatMemberRepository.findAllByChatRoomId(chatRoomId);

    if (chatMemberList.isEmpty()) {
      throw new CustomException(ErrorCode.CHAT_ROOM_IS_EMPTY);
    }

    return chatMemberList.stream().map(ChatMemberDto::from).collect(Collectors.toList());
  }
}
