package com.example.gotogetherbe.chat.config.handler;

import static com.example.gotogetherbe.global.exception.type.ErrorCode.ALREADY_DELETED_CHATROOM;
import static org.springframework.security.access.AccessDecisionVoter.ACCESS_DENIED;

import com.example.gotogetherbe.auth.service.AuthServiceImpl;
import com.example.gotogetherbe.chat.entity.ChatMember;
import com.example.gotogetherbe.chat.entity.ChatRoom;
import com.example.gotogetherbe.chat.repository.ChatMemberRepository;
import com.example.gotogetherbe.chat.repository.ChatRoomRepository;
import com.example.gotogetherbe.chat.type.ChatRoomStatus;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.global.util.jwt.TokenProvider;
import java.security.Principal;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompPreHandler implements ChannelInterceptor {

  private final TokenProvider tokenProvider;
  private final ChatRoomRepository chatRoomRepository;
  private final ChatMemberRepository chatMemberRepository;
  private final AuthServiceImpl authService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    assert accessor != null;

    if (StompCommand.CONNECT.equals(accessor.getCommand())) { // 연결 인증 처리
      String accessToken = accessor.getFirstNativeHeader("Authorization");

      if (tokenProvider.validateToken(accessToken)) {
        setAuthentication(accessToken, accessor);
      } else {
        // TODO : refresh Token

        setAuthentication(accessToken, accessor);
      }

      log.info("[WS] connection successful");
    } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) { // 채팅방 구독 권한 확인
      Long memberId = getMemberId(accessor.getUser());
      Long roomId = parseRoomId(accessor.getDestination());

      validateChatRoom(roomId);

      checkAccessChatRoom(roomId, memberId);

      // 현재 구독한 채팅방으로만 메세지 전송
      Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
      sessionAttributes.put("roomId", roomId);

      log.info("[WS] subscribed chat room [{}]", roomId);
    }

    return message;
  }

  private void setAuthentication(String accessToken, StompHeaderAccessor accessor) {
    Authentication authentication = tokenProvider.getAuthentication(accessToken);
    accessor.setUser(authentication);
  }

  private void validateChatRoom(Long roomId) {
    if (!chatRoomRepository.existsById(roomId)) {
      throw new GlobalException(ErrorCode.CHATROOM_NOT_FOUND);
    }
  }

  private void checkAccessChatRoom(Long chatRoomId, Long memberId) {
    ChatMember chatMember = chatMemberRepository.findByChatRoomIdAndMemberId(chatRoomId, memberId)
        .orElseThrow(() -> new GlobalException(ErrorCode.ACCESS_DENIED));

    if (chatMember.getChatRoom().getStatus() == ChatRoomStatus.DELETED) {
      throw new GlobalException(ALREADY_DELETED_CHATROOM);
    }
  }

  private Long getMemberId(Principal user) {
    if (ObjectUtils.isEmpty(user)) {
      throw new GlobalException(ErrorCode.USER_NOT_FOUND);
    }
    return Long.parseLong(user.getName());
  }

  private Long parseRoomId(String destination) {
    if (ObjectUtils.isEmpty(destination) || !destination.startsWith(CHAT_ROOM) ||
        destination.length() == CHAT_ROOM.length()) {
      throw new GlobalException(ErrorCode.USER_NOT_FOUND); // 잘못된 구독 경로
    }
    return Long.parseLong(destination.substring(CHAT_ROOM.length()));
  }
}
