package com.example.gotogetherbe.chat.config.handler;


import com.example.gotogetherbe.chat.dto.ChatRoomSessionDto;
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
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
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
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

    assert accessor != null;

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String token = resolveToken(accessor.getFirstNativeHeader("Authorization"));
      log.info("[preSend] stomp connection token : {}", token);

      accessor.setUser(tokenProvider.getAuthentication(token));

      log.info("[preSend] stomp connection => user : {}, sessionId : {}",
          Objects.requireNonNull(accessor.getUser()).getName(),
          accessor.getSessionId());
    } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
      log.info("[preSend] stomp subscribe. destination: {}, user: {}, sessionId : {}",
          accessor.getDestination(),
          Objects.requireNonNull(accessor.getUser()).getName(),
          accessor.getSessionId());

      String sessionId = accessor.getSessionId();

      Member member = memberRepository.findByEmail(accessor.getUser().getName())
          .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

      Long chatRoomId = getRoomIdFromDestination(Objects.requireNonNull(accessor.getDestination()));
      ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
          .orElseThrow(() -> new GlobalException(ErrorCode.CHATROOM_NOT_FOUND));

      saveSession(chatRoom.getId(), member.getId(), sessionId);
      log.info("[preSend] stomp subscribe redis 구독 저장 완료");
    } else if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {
      String sessionId = accessor.getSessionId();
      assert sessionId != null;

      ChatRoomSessionDto sessionDto = redisService.getChatRoomHashKey(ChatConstant.CHATROOM_SESSION, sessionId);
      if (sessionDto == null) {
        return message;
      }

      try {
        ChatRoom chatRoom = chatRoomRepository.findById(sessionDto.chatRoomId())
            .orElseThrow(() -> new GlobalException(ErrorCode.CHATROOM_NOT_FOUND));

        Member member = memberRepository.findById(sessionDto.memberId())
            .orElseThrow(() -> new GlobalException(ErrorCode.MEMBER_NOT_FOUND));

        //채팅방 뒤로 가기 진행 시에 마지막 메시지 ID 업데이트 진행
        chatMemberRepository.findByChatRoomIdAndMemberId(chatRoom.getId(), member.getId())
            .ifPresent(p ->
                chatMessageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(chatRoom.getId())
                    .ifPresent(chatMessage -> {
                      p.updateLastChatId(chatMessage.getId());
                      chatMemberRepository.save(p);
                      log.info("[preSend] stomp disconnect 마지막 메시지 저장 완료");
                    })
            );

        redisService.deleteHashKey(ChatConstant.CHATROOM_SESSION, sessionId);
        log.info("[preSend] stomp disconnect deleteHashKey 완료");

      } catch (Exception e) {
        log.error("[preSend] stomp disconnect is occurred : {}", e.getMessage());
      }

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
    return Long.parseLong(destination.substring(destination.lastIndexOf("/") + 1));
  }

  private void saveSession(Long chatRoomId, Long memberId, String sessionId) {
    ChatRoomSessionDto sessionDto = ChatRoomSessionDto.builder()
        .chatRoomId(chatRoomId)
        .memberId(memberId)
        .build();

    redisService.updateToHash(ChatConstant.CHATROOM_SESSION, sessionId, sessionDto);
  }
}
