package com.example.gotogetherbe.global.util.jwt.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {

  private String accessToken;
  private String refreshToken;
  private Long accessTokenExpireTime;
  private Long refreshTokenExpireTime;

}
