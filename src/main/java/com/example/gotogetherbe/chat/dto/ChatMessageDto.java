package com.example.gotogetherbe.chat.dto;

import com.example.gotogetherbe.chat.entity.ChatMessage;
import com.example.gotogetherbe.member.entitiy.Member;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
  private Long id;
  private String email;
  private Long chatRoomId;
  private String content;
  private String nickname;
  private String profileImageUrl;
  private LocalDateTime createdAt;

  public ChatMessage toEntity() {
    return ChatMessage.builder()
        .content(this.content)
        .createdAt(this.createdAt.plusHours(9))
        .build();
  }

  public static ChatMessageDto from(ChatMessage chatMessage, Member member) {
    return ChatMessageDto.builder()
        .id(chatMessage.getId())
        .email(member.getEmail())
        .chatRoomId(chatMessage.getChatRoom().getId())
        .content(chatMessage.getContent())
        .nickname(member.getNickname())
        .profileImageUrl(member.getProfileImageUrl())
        .createdAt(chatMessage.getCreatedAt())
        .build();
  }
}
