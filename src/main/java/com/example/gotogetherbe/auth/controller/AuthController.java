package com.example.gotogetherbe.auth.controller;

import com.example.gotogetherbe.auth.dto.LogoutDto;
import com.example.gotogetherbe.auth.dto.ReissueDto;
import com.example.gotogetherbe.auth.dto.SignInDto;
import com.example.gotogetherbe.auth.dto.SignUpDto;
import com.example.gotogetherbe.auth.kakao.dto.KaKaoSignUpDto;
import com.example.gotogetherbe.auth.kakao.service.KakaoService;
import com.example.gotogetherbe.auth.service.AuthService;
import com.example.gotogetherbe.global.util.jwt.dto.TokenDto;
import com.example.gotogetherbe.global.util.mail.dto.SendMailRequest;
import com.example.gotogetherbe.global.util.mail.dto.SendMailResponse;
import com.example.gotogetherbe.global.util.mail.dto.VerifyMailRequest;
import com.example.gotogetherbe.global.util.mail.service.MailService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {


  private final AuthService authService;
  private final MailService mailService;
  private final KakaoService kakaoService;

  @PostMapping("/signUp")
  @ApiResponse
  public ResponseEntity<SignUpDto> signUpUser(@RequestPart("request") SignUpDto request,
      @RequestPart(name = "image", required = false) MultipartFile image){
    return ResponseEntity.status(HttpStatus.CREATED).body(authService.signUp(request,image));
  }

  @PostMapping("/signIn")
  public ResponseEntity<TokenDto> signInUser(@RequestBody SignInDto request){
    return ResponseEntity.ok(authService.signIn(request));
  }
  @PostMapping("/logout")
  public ResponseEntity<?> logoutUser(@RequestBody LogoutDto request){
    authService.logout(request);
    return ResponseEntity.status(HttpStatus.OK).body("로그아웃 성공");
  }

  @PostMapping("/reissue")
  public ResponseEntity<TokenDto> reissueToken(@Valid @RequestBody ReissueDto request){
    return ResponseEntity.ok(authService.reissue(request));
  }
  @PostMapping("/mail/certification")
  public ResponseEntity<SendMailResponse> sendCertificationMail(@RequestBody SendMailRequest request){
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mailService.generateAndDispatchAuthCode(request.getEmail()));
  }
  @PostMapping("/mail/verify")
  public ResponseEntity<Boolean> sendVerifyMail(@RequestBody VerifyMailRequest request){

    return ResponseEntity.ok(mailService.verifyEmail(request.getEmail(), request.getCode()));
  }

  @GetMapping("/redirected/kakao")
  public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code){
    return ResponseEntity.ok(kakaoService.kakaoLogin(code));
  }

  @PostMapping("/kakao/signUp")
  public ResponseEntity<?> kakoSignUp(@RequestBody KaKaoSignUpDto request){
    return ResponseEntity.ok(kakaoService.kakaoSignUp(request));
  }
  @GetMapping("/checkEmail")
  public ResponseEntity<Boolean> checkEmail(@RequestParam("email") String email){
    return ResponseEntity.ok(authService.checkEmail(email));
  }

  @GetMapping("/checkNickname")
  public ResponseEntity<Boolean> checkNickname(@RequestParam("nickname") String nickname){
    return ResponseEntity.ok(authService.checkNickname(nickname));
  }
}
