package com.example.gotogetherbe.member.dto;

import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.entitiy.type.MemberGender;
import com.example.gotogetherbe.member.entitiy.type.MemberMbti;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {

  private Long id;

  private String email;

  private String password;

  private String name;

  private String nickname;

  private String phoneNumber;

  private String address;

  private Integer age;

  private MemberGender gender;

  private MemberMbti mbti;

  private String instagramId;

  private String description;

  private String profileImageUrl;

  public static MemberResponse fromEntity(Member member, boolean includeSensitiveInfo) {
    MemberResponse.MemberResponseBuilder builder = MemberResponse.builder()
        .id(member.getId())
        .email(member.getEmail())
        .name(member.getName())  // 수정
        .nickname(member.getNickname())
        .phoneNumber(member.getPhoneNumber())
        .address(member.getAddress())
        .gender(member.getGender())
        .age(member.getAge())
        .mbti(member.getMbti())
        .instagramId(member.getInstagramId())
        .description(member.getDescription())
        .profileImageUrl(member.getProfileImageUrl());

    if (includeSensitiveInfo) {
      builder.password(member.getPassword()); // Sensitive info
    }

    return builder.build();
  }
}
