package com.example.gotogetherbe.member.dto;

import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.entitiy.type.MemberGender;
import com.example.gotogetherbe.member.entitiy.type.MemberMbti;
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
public class MemberRequest {

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


  public void updateMemberInfoToEntity(Member member,String encodedPasswordEncoder
      ,String profileImageUrl) {
    member.setPassword(encodedPasswordEncoder);
    member.setNickname(this.nickname);
    member.setPhoneNumber(this.phoneNumber);
    member.setAddress(this.address);
    member.setAge(this.age);
    member.setGender(this.gender);
    member.setMbti(this.mbti);
    member.setInstagramId(this.instagramId);
    member.setDescription(this.description);
    member.setProfileImageUrl(profileImageUrl);

  }

}
