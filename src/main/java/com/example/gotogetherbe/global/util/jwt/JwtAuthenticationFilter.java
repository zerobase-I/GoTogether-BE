package com.example.gotogetherbe.global.util.jwt;


import static com.example.gotogetherbe.global.exception.type.ErrorCode.UNKNOWN_ERROR;

import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.service.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final TokenProvider tokenProvider;
  private final RedisService redisService;
  @Value("${spring.jwt.prefix}")
  private String tokenPrefix;

  @Value("${spring.jwt.header}")
  private String tokenHeader;


  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = resolveTokenFromRequest(request);

    try {

      if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
        // redis 에서 토큰에 대한 데이터가 없는 경우 (로그아웃 상태 아님)
        if (Objects.isNull(redisService.getData(token))) {
          // 토큰으로부터 인증 정보를 받아옴
          Authentication authentication = tokenProvider.getAuthentication(token);
          log.info("getAuthentication method called with token: {}", token);

          // SecurityContext 에 인증 정보를 저장
          SecurityContextHolder.getContext().setAuthentication(authentication);
          log.info("Authentication object stored in SecurityContext: {}", authentication);

        } else { // redis 에서 토큰에 대한 데이터가 있는 경우 (로그아웃 상태)
          request.setAttribute("exception", UNKNOWN_ERROR);
        }
      }
    } catch (GlobalException e) {
      request.setAttribute("exception", UNKNOWN_ERROR);
    }

    filterChain.doFilter(request, response);
  }


  private String resolveTokenFromRequest(HttpServletRequest request) {
    String token = request.getHeader(this.tokenHeader);
    if (!ObjectUtils.isEmpty(token) && token.startsWith(this.tokenPrefix)) {
      return token.substring(this.tokenPrefix.length());
    }
    return null;
  }
}
