package com.example.gotogetherbe.chat.config;

import com.example.gotogetherbe.chat.type.ChatConstant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

  @Value("${spring.rabbitmq.host}")
  private String host;

  @Value("${spring.rabbitmq.virtual-host}")
  private String virtualHost;

  @Value("${spring.rabbitmq.port}")
  private int port;

  @Value("${spring.rabbitmq.username}")
  private String username;

  @Value("${spring.rabbitmq.password}")
  private String password;

  /**
   * Queue 등록
   */
  @Bean
  public Queue queue() {
    return new Queue(ChatConstant.CHAT_QUEUE_NAME,true);
  }

  /**
   * exchange 등록
   */
  @Bean
  public TopicExchange topicExchange() {
    return new TopicExchange(ChatConstant.CHAT_EXCHANGE_NAME, true, false);
  }

  /**
   * Exchange와 queue 바인딩
   */
  @Bean
  public Binding binding() {
    return BindingBuilder.bind(queue())
        .to(topicExchange())
        .with(ChatConstant.ROUTING_KEY);
  }

  /**
   * RabbitMQ 연결을 관리하는 클래스
   */
  @Bean
  public ConnectionFactory connectionFactory() {
    CachingConnectionFactory factory = new CachingConnectionFactory();
    factory.setHost(host);
    factory.setPort(port);
    factory.setVirtualHost(virtualHost);
    factory.setUsername(username);
    factory.setPassword(password);
    return factory;
  }

  /**
   * MessageConverter 커스터마이징 하기 위한 Bean 등록
   */
  @Bean
  public RabbitTemplate rabbitTemplate() {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    rabbitTemplate.setRoutingKey(ChatConstant.ROUTING_KEY);
    return rabbitTemplate;
  }

  /**
   * LocalDateTime Serializable 설정 컨버팅
   */
  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  /**
   * RabbitMQ MessageListener 설정
   */
  @Bean
  public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory() {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory());
    factory.setMessageConverter(jsonMessageConverter());
    return factory;
  }
}
