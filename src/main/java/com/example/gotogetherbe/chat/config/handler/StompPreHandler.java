package com.example.gotogetherbe.chat.config.handler;


import com.example.gotogetherbe.chat.entity.ChatRoom;
import com.example.gotogetherbe.chat.repository.ChatMemberRepository;
import com.example.gotogetherbe.chat.repository.ChatMessageRepository;
import com.example.gotogetherbe.chat.repository.ChatRoomRepository;
import com.example.gotogetherbe.chat.type.ChatConstant;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.global.service.RedisService;
import com.example.gotogetherbe.global.util.jwt.TokenProvider;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompPreHandler implements ChannelInterceptor {

  @Value("${spring.jwt.prefix}")
  private String ACCESS_TOKEN_PREFIX;
  private static final String REFRESH_TOKEN_PREFIX = "RT: ";

  private final TokenProvider tokenProvider;

  private final MemberRepository memberRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMemberRepository chatMemberRepository;
  private final ChatMessageRepository chatMessageRepository;

  private final RedisService redisService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    assert accessor != null;

    // Stomp 연결시 인증 처리
    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      log.info("[WS] connection start");
      String token = resolveToken(accessor.getFirstNativeHeader("Authorization"));

      if (tokenProvider.validateToken(token)) {
        setAuthentication(token, accessor);
      }
      else {
        String refreshToken = redisService.getData(REFRESH_TOKEN_PREFIX + token);

        tokenProvider.validateToken(refreshToken);

        setAuthentication(token, accessor);
      }
      log.info("[WS] connection successful");

    } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) { // 채팅방 구독 권한 확인
      log.info("[WS] subscribe start");
      String email = accessor.getUser().getName();
      Long roomId = parseRoomId(accessor.getDestination());

      Member member = memberRepository.findByEmail(email)
          .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

      if (!chatRoomRepository.existsById(roomId)) {
        throw new GlobalException(ErrorCode.CHATROOM_NOT_FOUND);
      }
      if (!chatMemberRepository.existsByChatRoomIdAndMemberId(roomId, member.getId())) {
        throw new GlobalException(ErrorCode.NOT_BELONG_TO_CHAT_MEMBER);
      }

      Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
      sessionAttributes.put("chatRoomId", roomId);

      log.info("[WS] subscribed chat room [{}]", roomId);
    } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) { // 채팅방 나갈 시 마지막 메세지 ID 업데이트
      String email = accessor.getUser().getName();
      Long roomId = parseRoomId(accessor.getDestination());

      Member member = memberRepository.findByEmail(email)
          .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

      if (!chatRoomRepository.existsById(roomId)) {
        throw new GlobalException(ErrorCode.CHATROOM_NOT_FOUND);
      }

      chatMemberRepository.findByChatRoomIdAndMemberId(roomId, member.getId()).ifPresent(
          p -> chatMessageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(roomId)
              .ifPresent(chatMessage -> {
                p.updateLastChatId(chatMessage.getId());
                chatMemberRepository.save(p);
                log.info("[WS] disconnect : Last Message save.");
              })
      );
    }

    return message;
  }

  private void setAuthentication(String token, StompHeaderAccessor accessor) {
    Authentication authentication = tokenProvider.getAuthentication(token);
    accessor.setUser(authentication);
  }

  private String resolveToken(String token) {
    if (StringUtils.hasText(token) && token.startsWith(ACCESS_TOKEN_PREFIX)) {
      return token.substring(ACCESS_TOKEN_PREFIX.length());
    }
    throw new GlobalException(ErrorCode.UNSUPPORTED_TOKEN);
  }

  private Long parseRoomId(String destination) {
    if (ObjectUtils.isEmpty(destination) || !destination.startsWith(ChatConstant.CHAT_ROOM) ||
        destination.length() == ChatConstant.CHAT_ROOM.length()) {
      throw new GlobalException(ErrorCode.WRONG_DESTINATION);
    }
    return Long.parseLong(destination.substring(ChatConstant.CHAT_ROOM.length()));
  }
}
