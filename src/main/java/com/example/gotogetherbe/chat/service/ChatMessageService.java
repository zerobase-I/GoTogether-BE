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

  /**
   * 채팅 메세지 작성
   *
   * @param request 채팅 메세지 입력값
   * @param chatRoomId 전송할 채팅방 아이디
   * @return 채팅 메세지
   */
  public ChatMessageDto chatMessage(ChatMessageDto request, Long chatRoomId) {
    Member member = getMember(request.getEmail());

    return ChatMessageDto.builder()
        .email(member.getEmail())
        .chatRoomId(chatRoomId)
        .content(request.getContent())
        .createdAt(LocalDateTime.now())
        .nickname(member.getNickname())
        .profileImageUrl(member.getProfileImageUrl())
        .build();
  }

  /**
   * 채팅방 입장 메세지 작성
   *
   * @param request 채팅 메세지 입력값
   * @param chatRoomId 전송할 채팅방 아이디
   * @return 채팅 메세지
   */
  public ChatMessageDto enterMessage(ChatMessageDto request, Long chatRoomId) {
    Member member = getMember(request.getEmail());

    String content = member.getNickname() + "님이 입장하였습니다.";

    return ChatMessageDto.builder()
        .email(member.getEmail())
        .chatRoomId(chatRoomId)
        .content(content)
        .createdAt(LocalDateTime.now())
        .nickname(member.getNickname())
        .profileImageUrl(member.getProfileImageUrl())
        .build();
  }

  /**
   * 채팅방 퇴장 메세지 작성
   *
   * @param request 채팅 메세지 입력값
   * @param chatRoomId 전송할 채팅방 아이디
   * @return 채팅 메세지
   */
  public ChatMessageDto exitMessage(ChatMessageDto request, Long chatRoomId) {
    Member member = getMember(request.getEmail());

    String content = member.getNickname() + "님이 퇴장하였습니다.";

    return ChatMessageDto.builder()
        .email(member.getEmail())
        .chatRoomId(chatRoomId)
        .content(content)
        .createdAt(LocalDateTime.now())
        .nickname(member.getNickname())
        .profileImageUrl(member.getProfileImageUrl())
        .build();
  }

  /**
   * 채팅방 메세지 저장
   *
   * @param response 채팅 전송 후 저장할 응답
   */
  public void saveChatMessage(ChatMessageDto response) {
    Member member = getMember(response.getEmail());
    chatMemberRepository.findByChatRoomIdAndMemberId(response.getChatRoomId(), member.getId())
        .ifPresent(e -> {
          ChatMessage message = response.toEntity();
          message.updateChatMember(e);
          message.updateChatRoom(e.getChatRoom());

          chatMessageRepository.save(message);
        });
  }

  private Member getMember(String email) {
    return memberRepository.findByEmail(email)
        .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
  }
}
