package com.example.gotogetherbe.auth.service;


import com.example.gotogetherbe.auth.dto.LogoutDto;
import com.example.gotogetherbe.auth.dto.ReissueDto;
import com.example.gotogetherbe.auth.dto.SignInDto;
import com.example.gotogetherbe.auth.dto.SignUpDto;
import com.example.gotogetherbe.global.util.jwt.dto.TokenDto;

public interface AuthService {

  SignUpDto signUp(SignUpDto request);

  TokenDto signIn(SignInDto request);

  void logout(LogoutDto request);

  TokenDto reissue(ReissueDto reissueDto);

}
