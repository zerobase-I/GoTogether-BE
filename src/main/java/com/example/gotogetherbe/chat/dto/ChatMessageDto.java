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
  private Long memberId;
  private Long chatRoomId;
  private String content;
  private String nickName;
  private String profileImageUrl;
  private LocalDateTime createdAt;

  public ChatMessage toEntity() {
    return ChatMessage.builder()
        .content(this.content)
        .createdAt(this.createdAt)
        .build();
  }
}
