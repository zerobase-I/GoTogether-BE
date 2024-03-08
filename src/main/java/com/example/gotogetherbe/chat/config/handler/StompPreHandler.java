package com.example.gotogetherbe.chat.config.handler;


import com.example.gotogetherbe.chat.dto.ChatRoomSessionDto;
import com.example.gotogetherbe.chat.entity.ChatMember;
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
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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

  private static final String ACCESS_TOKEN_PREFIX = "Bearer ";

  private final TokenProvider tokenProvider;

  private final MemberRepository memberRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMemberRepository chatMemberRepository;
  private final ChatMessageRepository chatMessageRepository;

  private final RedisService redisService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    log.info("[WS] connection start");
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    assert accessor != null;

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String accessToken = accessor.getFirstNativeHeader("Authorization");

      String token = resolveToken(accessToken);

      if (tokenProvider.validateToken(token)) {
        Authentication authentication = tokenProvider.getAuthentication(token);
        accessor.setUser(authentication);
      }
      /*else {
          refresh token
      }*/
      log.info("[WS] connection successful");

    } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
      String email = accessor.getUser().getName();
      Long roomId = parseRoomId(accessor.getDestination());

      Member member = memberRepository.findByEmail(email)
          .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

      ChatRoom chatRoom = chatRoomRepository.findById(roomId)
          .orElseThrow(() -> new GlobalException(ErrorCode.CHATROOM_NOT_FOUND));

      ChatMember chatmember = chatMemberRepository.findByChatRoomIdAndMemberId(chatRoom.getId(), member.getId())
          .orElseThrow(() -> new GlobalException(ErrorCode.NOT_BELONG_TO_CHAT_MEMBER));

      Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
      sessionAttributes.put("chatRoomId", roomId);

      log.info("[WS] subscribed chat room [{}]", roomId);
    }

    return message;
  }

  private String resolveToken(String token) {
    if (StringUtils.hasText(token) && token.startsWith(ACCESS_TOKEN_PREFIX)) {
      return token.substring(ACCESS_TOKEN_PREFIX.length());
    }
    throw new GlobalException(ErrorCode.UNSUPPORTED_TOKEN);
  }

  private Long getRoomIdFromDestination(String destination) {
    return Long.parseLong(destination.substring(destination.lastIndexOf(".") + 1));
  }

  private void saveSession(Long chatRoomId, Long memberId, String sessionId) {
    ChatRoomSessionDto sessionDto = ChatRoomSessionDto.builder()
        .chatRoomId(chatRoomId)
        .memberId(memberId)
        .build();

    redisService.updateToHash(ChatConstant.CHATROOM_SESSION, sessionId, sessionDto);
  }

  private Long parseRoomId(String destination) {
    if (ObjectUtils.isEmpty(destination) || !destination.startsWith("/exchange/chat.exchange/room.") ||
        destination.length() == "/exchange/chat.exchange/room.".length()) {
      throw new RuntimeException("목적지 오류");
    }
    return Long.parseLong(destination.substring("/exchange/chat.exchange/room.".length()));
  }
}
