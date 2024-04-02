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

  public static ChatMessageDto from(ChatMessage chatMessage) {
    return ChatMessageDto.builder()
        .id(chatMessage.getId())
        .email(chatMessage.getChatMember().getMember().getEmail())
        .chatRoomId(chatMessage.getChatRoom().getId())
        .content(chatMessage.getContent())
        .nickname(chatMessage.getChatMember().getMember().getNickname())
        .profileImageUrl(chatMessage.getChatMember().getMember().getProfileImageUrl())
        .createdAt(chatMessage.getCreatedAt())
        .build();
  }
}
