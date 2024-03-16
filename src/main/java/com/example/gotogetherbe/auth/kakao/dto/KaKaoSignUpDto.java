package com.example.gotogetherbe.auth.kakao.dto;

import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.entitiy.type.MemberGender;
import com.example.gotogetherbe.member.entitiy.type.MemberLoginType;
import com.example.gotogetherbe.member.entitiy.type.MemberMbti;

import com.example.gotogetherbe.member.entitiy.type.MemberRoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KaKaoSignUpDto {

  @Email
  private String email;
  @NotBlank
  private String name;
  @NotNull
  private MemberGender gender;

  @NotBlank
  private String nickname;
  @NotBlank
  private String phoneNumber;
  @NotBlank
  private String address;
  @NotNull
  private Integer age;

  private MemberMbti mbti;
  private String instagramId;
  private String description;

  public static KaKaoSignUpDto fromEntity(Member member){
    return KaKaoSignUpDto.builder()
        .name(member.getName())
        .nickname(member.getNickname())
        .phoneNumber(member.getPhoneNumber())
        .address(member.getAddress())
        .age(member.getAge())
        .mbti(member.getMbti())
        .instagramId(member.getInstagramId())
        .description(member.getDescription())
        .build();
  }
  public static Member toEntity(KaKaoSignUpDto request){
    return Member.builder()
        .email(request.getEmail())
        .name(request.getName())
        .nickname(request.getNickname())
        .gender(request.getGender())
        .phoneNumber(request.getPhoneNumber())
        .address(request.getAddress())
        .age(request.getAge())
        .mbti(request.getMbti())
        .instagramId(request.getInstagramId())
        .description(request.getDescription())
        .roleType(MemberRoleType.USER)
        .loginType(MemberLoginType.KAKAO)
        .emailAuth(true)
        .build();
  }

}
