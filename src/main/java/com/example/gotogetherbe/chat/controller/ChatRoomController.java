package com.example.gotogetherbe.chat.controller;

import com.example.gotogetherbe.auth.config.LoginUser;
import com.example.gotogetherbe.chat.dto.ChatMemberDto;
import com.example.gotogetherbe.chat.dto.ChatMessageDto;
import com.example.gotogetherbe.chat.dto.ChatRoomDto;
import com.example.gotogetherbe.chat.service.ChatRoomService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-room")
public class ChatRoomController {
  private final ChatRoomService chatRoomService;

  // 채팅방 생성
  @PostMapping("/{postId}")
  public ResponseEntity<ChatRoomDto> createChatRoom(
      @LoginUser String username,
      @PathVariable Long postId
  ) {
    return ResponseEntity.ok(chatRoomService.createChatRoom(username, postId));
  }

  // 참여중인 채팅방 목록 조회
  @GetMapping("/list")
  public ResponseEntity<List<ChatRoomDto>> getChatRoomList(@LoginUser String username) {
    return ResponseEntity.ok(chatRoomService.getMyChatRoomList(username));
  }

  // 채팅방 메세지 조회
  @GetMapping("/message/{chatRoomId}")
  public ResponseEntity<List<ChatMessageDto>> getChatRoomMessage(
      @LoginUser String username,
      @PathVariable Long chatRoomId) {
    return ResponseEntity.ok(chatRoomService.getMyChatRoomMessage(username, chatRoomId));
  }

  // 채팅방 입장
  @PostMapping("/enter/{chatRoomId}")
  public ResponseEntity<ChatMemberDto> enterChatRoom(
      @LoginUser String username,
      @PathVariable Long chatRoomId
  ) {
    return ResponseEntity.ok(chatRoomService.enterChatRoom(username, chatRoomId));
  }

  // 채팅방 퇴장
  @DeleteMapping("/exit/{chatRoomId}")
  public ResponseEntity<ChatMemberDto> exitChatRoom(
      @LoginUser String username,
      @PathVariable Long chatRoomId
  ) {
    return ResponseEntity.ok(chatRoomService.exitChatRoom(username, chatRoomId));
  }
}
