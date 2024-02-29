package com.example.gotogetherbe.global.util.jwt.dto;

import com.example.gotogetherbe.member.entitiy.Member;
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
public class CustomUserDto {

  private Long id;
  private String email;
  private String password;
  private String roleType;

  public static CustomUserDto fromEntity(Member member){
    return CustomUserDto.builder()
        .id(member.getId())
        .email(member.getEmail())
        .password(member.getPassword())
        .roleType(member.getRoleType().getCode())
        .build();
  }

}
