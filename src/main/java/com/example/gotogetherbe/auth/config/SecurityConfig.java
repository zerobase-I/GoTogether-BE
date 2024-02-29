package com.example.gotogetherbe.auth.config;


import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import com.example.gotogetherbe.global.util.jwt.JwtAccessDeniedHandler;
import com.example.gotogetherbe.global.util.jwt.JwtAuthenticationEntryPoint;
import com.example.gotogetherbe.global.util.jwt.JwtAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement((sessionManagement) ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .authorizeHttpRequests(
            auth -> auth
                .requestMatchers(requestHasRoleUser()).hasRole("USER")
                .requestMatchers(requestHasRoleAdmin()).hasRole("ADMIN")
                .requestMatchers(requestHasAnyRoleUserAdmin()).hasAnyRole("USER", "ADMIN")
                .anyRequest().permitAll()
        ).exceptionHandling(configurer -> {
          configurer.authenticationEntryPoint(jwtAuthenticationEntryPoint);
          configurer.accessDeniedHandler(jwtAccessDeniedHandler);
        })
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return httpSecurity.build();
  }

  private RequestMatcher[] requestHasRoleUser() {
    List<RequestMatcher> requestMatchers = List.of(
        antMatcher("/api/user"),
        antMatcher(POST, "/api/post"),
        antMatcher(PUT, "/api/post"),
        antMatcher(DELETE, "/api/post")

    );
    return requestMatchers.toArray(RequestMatcher[]::new);
  }

  private RequestMatcher[] requestHasRoleAdmin() {
    List<RequestMatcher> requestMatchers = List.of(

    );

    return requestMatchers.toArray(RequestMatcher[]::new);
  }

  private RequestMatcher[] requestHasAnyRoleUserAdmin() {
    List<RequestMatcher> requestMatchers = List.of(
        antMatcher(DELETE, "/api/comment")
    );
    return requestMatchers.toArray(RequestMatcher[]::new);
  }
}
