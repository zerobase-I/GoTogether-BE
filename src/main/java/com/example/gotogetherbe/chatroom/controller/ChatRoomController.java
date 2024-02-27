package com.example.gotogetherbe.chatroom.controller;

import com.example.gotogetherbe.chatroom.dto.CreateChatRoomForm;
import com.example.gotogetherbe.chatroom.service.impl.ChatRoomServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-room")
public class ChatRoomController {
  private final ChatRoomServiceImpl chatRoomService;

  // 채팅방 생성
  @PostMapping("/{postId}")
  public ResponseEntity<?> createChatRoom(
      /* 회원인증 */
      @PathVariable Long postId,
      @RequestBody CreateChatRoomForm form
  ) {
    return ResponseEntity.ok(chatRoomService.createChatRoom(email, postId, form.getName()));
  }

  // 참여중인 채팅방 목록 조회
  @GetMapping("/list")
  public ResponseEntity<?> getChatRoomList(/* 회원인증 */) {
    return ResponseEntity.ok(chatRoomService.getChatRoomList(email));
  }

  // 채팅방 입장
  @PostMapping("/{chatRoomId}")
  public ResponseEntity<?> enterChatRoom(
      /* 회원인증 */
      @PathVariable Long chatRoomId
  ) {
    return ResponseEntity.ok(chatRoomService.enterChatRoom(email, chatRoomId));
  }

  // 채팅방 퇴장
  @DeleteMapping("/{chatRoomId}")
  public ResponseEntity<?> exitChatRoom(
      /* 회원인증 */
      @PathVariable Long chatRoomId
  ) {
    return ResponseEntity.ok(chatRoomService.exitChatRoom(email, chatRoomId));;
  }
}
