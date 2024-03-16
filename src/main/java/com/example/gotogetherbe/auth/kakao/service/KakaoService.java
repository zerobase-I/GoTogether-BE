package com.example.gotogetherbe.auth.kakao.service;

import static com.example.gotogetherbe.global.exception.type.ErrorCode.DUPLICATE_NICKNAME;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.DUPLICATE_USER;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_NOT_FOUND;

import com.example.gotogetherbe.auth.kakao.config.KakaoApi;
import com.example.gotogetherbe.auth.kakao.dto.KaKaoSignUpDto;
import com.example.gotogetherbe.auth.kakao.dto.api.KakaoTokenApiResponse;
import com.example.gotogetherbe.auth.kakao.dto.api.KakaoUserInfoApiResponse;
import com.example.gotogetherbe.auth.service.AuthService;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.util.jwt.dto.TokenDto;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.entitiy.type.MemberLoginType;
import com.example.gotogetherbe.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoService {

  private final KakaoApi kakaoApi;
  private final MemberRepository memberRepository;
  private final AuthService authService;

  @Value("${spring.data.kakao.grant_type}")
  private  String grant_type;

  @Value("${spring.data.kakao.client_id}")
  private String client_id;

  @Value("${spring.data.kakao.redirect_uri}")
  private String redirect_uri;

  /**
   * 카카오 로그인 처리 메서드.
   *
   * @param code 카카오 로그인 인증 후 받은 코드
   * @return 로그인 처리 결과
   */
  public Object kakaoLogin(String code) {
    // 카카오 서버로부터 액세스 토큰을 받아온다.
    KakaoTokenApiResponse token = kakaoApi.getToken(grant_type, client_id, redirect_uri, code);
    String accessToken = token.getAccess_token();

    // 받아온 토큰을 사용하여 사용자 정보를 요청한다.
    KakaoUserInfoApiResponse userInfo = kakaoApi.getUserInfo("Bearer " + accessToken);
    try {
      // 이메일을 통해 기존 회원이 있는지 확인한다.
      Member member = memberRepository.findByEmail(userInfo.getKakaoAccount().getEmail())
          .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));

      // 카카오 로그인이 아닌 경우 에러 처리
      if(member.getLoginType() != MemberLoginType.KAKAO){
        throw new GlobalException(DUPLICATE_USER);
      }
      // 토큰 생성 및 반환
      return authService.generateToken(member.getEmail(), member.getRoleType().getCode());

    } catch (GlobalException e) {
      // 카카오로 처음 로그인(회원가입) 하는 경우, 사용자 정보를 반환하여 추가 정보 입력을 유도한다.
      if (USER_NOT_FOUND.equals(e.getErrorCode())) {
        return userInfo.getKakaoAccount();
      } else {
        throw e; // USER_NOT_FOUND 가 아닌 다른 GlobalException 은 그대로 다시 throw.
      }
    }
  }

  /**
   * 카카오 회원가입 처리 메서드.
   *
   * @param request 회원가입 요청 정보를 담은 DTO
   * @return 생성된 토큰 정보
   */

  public TokenDto kakaoSignUp(KaKaoSignUpDto request) {
    // 닉네임 중복 검사
    if(memberRepository.existsByNickname(request.getNickname())){
      throw new GlobalException(DUPLICATE_NICKNAME);
    }

    // DTO 로부터 Member 엔티티 생성
    Member member = KaKaoSignUpDto.toEntity(request);

    // 회원 정보 저장
    memberRepository.save(member);

    // 토큰 생성 및 반환
    return authService.generateToken(member.getEmail(), member.getRoleType().getCode());
  }

}

