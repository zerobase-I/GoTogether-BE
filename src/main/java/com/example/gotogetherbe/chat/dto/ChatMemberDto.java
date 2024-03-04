package com.example.gotogetherbe.chat.dto;

import com.example.gotogetherbe.chat.entity.ChatMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemberDto {
  private Long memberId;
  private String nickName;
  private String profileUrl;

  public static ChatMemberDto from(ChatMember chatMember) {
    return ChatMemberDto.builder()
        .memberId(chatMember.getMember().getId())
        .nickName(chatMember.getMember().getNickname())
        .profileUrl(chatMember.getMember().getProfileImageUrl())
        .build();
  }
}
