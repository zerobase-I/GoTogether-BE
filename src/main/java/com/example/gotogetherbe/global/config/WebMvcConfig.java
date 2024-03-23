package com.example.gotogetherbe.global.config;


import com.example.gotogetherbe.auth.config.LoginUserArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

  private final LoginUserArgumentResolver loginUserArgumentResolver;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    // loginUserArgumentResolver 를 Argument Resolver 목록에 추가
    resolvers.add(loginUserArgumentResolver);
  }
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("https://go-together-frontend.vercel.app/member")  // 여기서 요청을 허용할 출처를 지정
        .allowedOrigins("http://localhost:5173")  // 여기서 요청을 허용할 출처를 지정
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용할 HTTP 메소드 지정
        .allowedHeaders("*")  // 모든 헤더 허용
        .allowCredentials(true)  // 쿠키를 포함한 요청 허용
        .maxAge(3600);
  }

}
