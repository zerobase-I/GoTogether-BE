package com.example.gotogetherbe.global.util.mail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMailRequest {

  @NotBlank(message = "이메일을 입력해주세요.")
  @Email(message = "형식에 맞지 않는 이메일입니다.")
  private String email;
}
