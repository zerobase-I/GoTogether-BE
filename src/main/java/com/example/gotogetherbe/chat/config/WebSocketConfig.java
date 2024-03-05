package com.example.gotogetherbe.chat.config;

import com.example.gotogetherbe.chat.config.handler.StompErrorHandler;
import com.example.gotogetherbe.chat.config.handler.StompPreHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // stomp 사용하기 위한 어노테이션
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final StompErrorHandler stompErrorHandler;
  private final StompPreHandler stompPreHandler;

  @Value("${spring.rabbitmq.host}")
  private String host;

  // 엔드포인트 등록
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/chat")
        .setAllowedOriginPatterns("*")
        .withSockJS();
    registry
        .setErrorHandler(stompErrorHandler);
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/pub");
    registry.enableStompBrokerRelay("/sub")
        .setRelayHost(host);
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(stompPreHandler);
  }
}
