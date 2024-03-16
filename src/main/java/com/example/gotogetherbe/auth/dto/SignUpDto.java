package com.example.gotogetherbe.auth.dto;



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
public class SignUpDto {

  @Email
  private String email;

  @NotBlank
  private String password;

  @NotBlank
  private String name;

  @NotBlank
  private String nickname;

  @NotBlank
  private String phoneNumber;

  @NotBlank
  private String address;

  @NotNull
  private Integer age;

  @NotNull
  private MemberGender gender;

  private MemberMbti mbti;

  private String instagramId;

  private String description;

  private String profileImageUrl;


  public static SignUpDto fromEntity(Member member){
    return SignUpDto.builder()
        .email(member.getEmail())
        .password(member.getPassword())
        .name(member.getName())
        .nickname(member.getNickname())
        .phoneNumber(member.getPhoneNumber())
        .address(member.getAddress())
        .age(member.getAge())
        .gender(member.getGender())
        .mbti(member.getMbti())
        .instagramId(member.getInstagramId())
        .description(member.getDescription())
        .profileImageUrl(member.getProfileImageUrl())
        .build();
  }

  public static Member signUpForm(SignUpDto request, String encodedPasswordEncoder){
    return Member.builder()
        .email(request.getEmail())
        .password(encodedPasswordEncoder)
        .name(request.getName())
        .nickname(request.getNickname())
        .phoneNumber(request.getPhoneNumber())
        .address(request.getAddress())
        .age(request.getAge())
        .gender(request.getGender())
        .mbti(request.getMbti())
        .instagramId(request.getInstagramId())
        .description(request.getDescription())
        .roleType(MemberRoleType.USER)
        .loginType(MemberLoginType.EMAIL)
        .profileImageUrl(request.getProfileImageUrl())  // 새로 추가
        .build();
  }

}
