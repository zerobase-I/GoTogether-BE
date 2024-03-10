package com.example.gotogetherbe.chat.config;
import com.example.gotogetherbe.chat.config.handler.StompErrorHandler;
import com.example.gotogetherbe.chat.config.handler.StompPreHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker // stomp 사용하기 위한 어노테이션
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final StompErrorHandler stompErrorHandler;
  private final StompPreHandler stompPreHandler;

  @Value("${relay.host}")
  private String relayHost;

  @Value("${relay.port}")
  private int relayPort;

  @Value("${relay.clientLogin}")
  private String clientLogin;

  @Value("${relay.clientPasscode}")
  private String clientPasscode;

  @Value("${relay.systemLogin}")
  private String systemLogin;

  @Value("${relay.systemPasscode}")
  private String systemPasscode;
  // 엔드포인트 등록
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
        .addEndpoint("/ws")
        .setAllowedOriginPatterns("*");
        //.withSockJS();
    registry
        .setErrorHandler(stompErrorHandler);
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // 메세지 발행 url
    registry.setApplicationDestinationPrefixes("/pub");
    // 메세지 구독 url
    registry.enableStompBrokerRelay("/exchange")
        .setRelayHost(relayHost)
        .setRelayPort(relayPort)
        .setClientLogin(clientLogin)
        .setClientPasscode(clientPasscode)
        .setSystemLogin(systemLogin)
        .setSystemPasscode(systemPasscode);

  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(stompPreHandler);
  }

  @Override
  public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
    registry.setMessageSizeLimit(50 * 1024 * 1024);
    registry.setSendTimeLimit(20 * 10000);
    registry.setSendBufferSizeLimit(3 * 512 * 1024);
  }

  @Bean
  public ServletServerContainerFactoryBean servletServerContainerFactoryBean(){
    ServletServerContainerFactoryBean servletServerContainerFactoryBean = new ServletServerContainerFactoryBean();
    servletServerContainerFactoryBean.setMaxTextMessageBufferSize(2048 * 2048);
    servletServerContainerFactoryBean.setMaxBinaryMessageBufferSize(2048 * 2048);
    return servletServerContainerFactoryBean;
  }
}
