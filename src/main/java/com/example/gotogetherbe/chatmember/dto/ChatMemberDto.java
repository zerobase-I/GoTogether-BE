package com.example.gotogetherbe.chatmember.dto;

import com.example.gotogetherbe.chatmember.entity.ChatMember;
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
        .nickName(chatMember.getMember().getNickName())
        .profileUrl(chatMember.getMember().getProfileUrl())
        .build()
  }
}
