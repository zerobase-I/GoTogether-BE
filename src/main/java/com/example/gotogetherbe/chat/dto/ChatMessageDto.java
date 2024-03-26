package com.example.gotogetherbe.chat.dto;

import com.example.gotogetherbe.chat.entity.ChatMessage;
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
  private String email;
  private Long chatRoomId;
  private String content;
  private String nickname;
  private String profileImageUrl;
  private LocalDateTime createdAt;
  private LocalDateTime timeStamp;

  public ChatMessage toEntity() {
    return ChatMessage.builder()
        .content(this.content)
        .createdAt(this.createdAt)
        .build();
  }

  public static ChatMessageDto from(ChatMessage chatMessage) {
    return ChatMessageDto.builder()
        .email(chatMessage.getChatMember().getMember().getEmail())
        .chatRoomId(chatMessage.getChatRoom().getId())
        .content(chatMessage.getContent())
        .nickname(chatMessage.getChatMember().getMember().getNickname())
        .profileImageUrl(chatMessage.getChatMember().getMember().getProfileImageUrl())
        .createdAt(chatMessage.getCreatedAt())
        .timeStamp(null)
        .build();
  }
}
