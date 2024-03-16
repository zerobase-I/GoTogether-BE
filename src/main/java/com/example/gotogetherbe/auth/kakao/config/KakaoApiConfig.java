package com.example.gotogetherbe.auth.kakao.config;

import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@Slf4j
public class KakaoApiConfig {

  private final int MAX_IN_MEMORY_SIZE = 10 * 10 * 10 * 1024;   //10MB
  @Bean
  public KakaoApi apiMovieProxy() {
    WebClient client = WebClient.builder()
        .baseUrl("")
        .defaultStatusHandler(HttpStatusCode::isError,
            resp -> {
              throw new GlobalException(ErrorCode.API_SERVER_ERROR);
            })
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_SIZE))
        .build();

    HttpServiceProxyFactory factory = HttpServiceProxyFactory
        .builder(WebClientAdapter.forClient(client))
        .build();

    return factory.createClient(KakaoApi.class);
  }
}
