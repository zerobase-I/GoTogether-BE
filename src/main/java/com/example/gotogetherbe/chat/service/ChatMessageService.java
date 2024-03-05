package com.example.gotogetherbe.chat.service;

import com.example.gotogetherbe.chat.dto.ChatMessageDto;
import com.example.gotogetherbe.chat.entity.ChatMessage;
import com.example.gotogetherbe.chat.repository.ChatMemberRepository;
import com.example.gotogetherbe.chat.repository.ChatMessageRepository;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {
  private final MemberRepository memberRepository;
  private final ChatMessageRepository chatMessageRepository;
  private final ChatMemberRepository chatMemberRepository;

  public ChatMessageDto chatMessage(ChatMessageDto request, Long chatRoomId) {
    Member member = memberRepository.findById(request.getMemberId())
        .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

    return ChatMessageDto.builder()
        .memberId(member.getId())
        .chatRoomId(chatRoomId)
        .content(request.getContent())
        .createdAt(LocalDateTime.now())
        .nickName(member.getNickname())
        .profileImageUrl(member.getProfileImageUrl())
        .build();
  }

  public ChatMessageDto enterMessage(ChatMessageDto request, Long chatRoomId) {
    Member member = memberRepository.findById(request.getMemberId())
        .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

    String content = member.getNickname() + "님이 입장하였습니다.";

    return ChatMessageDto.builder()
        .memberId(member.getId())
        .chatRoomId(chatRoomId)
        .content(content)
        .createdAt(LocalDateTime.now())
        .nickName(member.getNickname())
        .profileImageUrl(member.getProfileImageUrl())
        .build();
  }

  public ChatMessageDto exitMessage(ChatMessageDto request, Long chatRoomId) {
    Member member = memberRepository.findById(request.getMemberId())
        .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

    String content = member.getNickname() + "님이 퇴장하였습니다.";

    return ChatMessageDto.builder()
        .memberId(member.getId())
        .chatRoomId(chatRoomId)
        .content(content)
        .createdAt(LocalDateTime.now())
        .nickName(member.getNickname())
        .profileImageUrl(member.getProfileImageUrl())
        .build();
  }

  public void saveChatMessage(ChatMessageDto response) {
    chatMemberRepository.findByChatRoomIdAndMemberId(response.getChatRoomId(), response.getMemberId())
        .ifPresent(e -> {
          ChatMessage message = response.toEntity();
          message.updateChatMember(e);
          message.updateChatRoom(e.getChatRoom());

          chatMessageRepository.save(message);
        });
  }
}
