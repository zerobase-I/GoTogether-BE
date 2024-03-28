package com.example.gotogetherbe.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 *  애플리케이션의 JSON 처리를 위한 설정을 담당.
 *  Java 8 날짜/시간 API를 지원하고,
 *  날짜/시간을 타임스탬프 형식으로 쓰지 않는 ObjectMapper를 스프링 빈으로 등록
 */
@Configuration
public class AppConfig {
  @PostConstruct
  void started() {
    // 애플리케이션의 기본 타임존 설정
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
  }
  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    return objectMapper;
  }
}
