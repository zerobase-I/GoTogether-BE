package com.example.gotogetherbe.chat.controller;

import com.example.gotogetherbe.chat.dto.ChatMessageDto;
import com.example.gotogetherbe.chat.service.ChatMessageService;
import com.example.gotogetherbe.chat.type.ChatConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {
  private final ChatMessageService chatMessageService;
  private final RabbitTemplate rabbitTemplate;

  // /pub/chat/{chatRoomId} 로 요청하면 브로커를 통해 처리
  // /sub/chat.sub/room.{chatRoomId} 를 구독한 클라이언트에 메세지 전송
  @MessageMapping("/chat/enter/{chatRoomId}")
  public void enterMember(@Payload ChatMessageDto chatMessageDto,@DestinationVariable("chatRoomId") Long chatRoomId) {
    rabbitTemplate.convertAndSend(ChatConstant.CHAT_EXCHANGE_NAME,"room." + chatRoomId, chatMessageService.enterMessage(chatMessageDto, chatRoomId));
  }

  @MessageMapping("/chat/exit/{chatRoomId}")
  public void exitMember(@Payload ChatMessageDto chatMessageDto,@DestinationVariable("chatRoomId") Long chatRoomId) {
    rabbitTemplate.convertAndSend(ChatConstant.CHAT_EXCHANGE_NAME,"room." + chatRoomId, chatMessageService.exitMessage(chatMessageDto, chatRoomId));
  }

  @MessageMapping("/chat/{chatRoomId}")
  public void sendMessage(@Payload ChatMessageDto chatMessageDto,@DestinationVariable("chatRoomId") Long chatRoomId) {
    rabbitTemplate.convertAndSend(ChatConstant.CHAT_EXCHANGE_NAME,"room." + chatRoomId, chatMessageService.chatMessage(chatMessageDto, chatRoomId));
  }

  @RabbitListener(queues = ChatConstant.CHAT_QUEUE_NAME)
  public void receive(ChatMessageDto response) {
    chatMessageService.saveChatMessage(response);
  }
}
