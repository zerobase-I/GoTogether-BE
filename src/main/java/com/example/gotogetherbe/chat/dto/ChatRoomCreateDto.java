package com.example.gotogetherbe.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChatRoomCreateDto {
  private Long postId;
  private Long accompanyRequestMemberId;
}
