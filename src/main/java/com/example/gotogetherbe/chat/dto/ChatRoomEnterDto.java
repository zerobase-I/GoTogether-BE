package com.example.gotogetherbe.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ChatRoomEnterDto {
  private Long chatRoomId;
  private Long memberId;
}
