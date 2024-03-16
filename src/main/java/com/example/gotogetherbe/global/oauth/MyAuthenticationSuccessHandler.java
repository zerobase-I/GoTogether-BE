package com.example.gotogetherbe.global.oauth;

import com.example.gotogetherbe.global.util.jwt.TokenProvider;
import com.example.gotogetherbe.global.util.jwt.dto.TokenDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final TokenProvider tokenProvider;
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response, Authentication authentication) throws IOException,
      ServletException {

    // OAuth2User 로 캐스팅하여 인증된 사용자 정보를 가져온다.
    OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

    String email = oAuth2User.getAttribute("email");

    String provider = oAuth2User.getAttribute("provider");

    // CustomOAuth2UserService 에서 셋팅한 로그인한 회원 존재 여부를 가져온다.
    boolean isExist = Boolean.TRUE.equals(oAuth2User.getAttribute("exist"));

    String role = oAuth2User.getAuthorities().stream().
        findFirst()
        .orElseThrow(IllegalAccessError::new)
        .getAuthority();

    if (isExist) {
      // 회원이 존재하면 jwt token 발행을 시작한다.
      TokenDto token = tokenProvider.generateToken(email, role);
      log.info("jwtToken = {}", token.getAccessToken());

      // accessToken 을 쿼리스트링에 담는 url 을 만들어준다.
      String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/loginSuccess")
          .queryParam("accessToken", token.getAccessToken())
          .build()
          .encode(StandardCharsets.UTF_8)
          .toUriString();
      log.info("redirect 준비");

      // 로그인 확인 페이지로 리다이렉트 시킨다.
      getRedirectStrategy().sendRedirect(request, response, targetUrl);


    } else {

      String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:8080/loginSuccess")
          .queryParam("email", (String) oAuth2User.getAttribute("email"))
          .queryParam("provider", provider)
          .build()
          .encode(StandardCharsets.UTF_8)
          .toUriString();

      getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
  }
}
