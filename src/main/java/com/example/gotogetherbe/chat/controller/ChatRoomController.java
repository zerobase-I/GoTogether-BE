package com.example.gotogetherbe.chat.controller;

import com.example.gotogetherbe.chat.dto.CreateChatRoomForm;
import com.example.gotogetherbe.chat.service.ChatRoomService;
import com.example.gotogetherbe.global.util.jwt.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  private final ChatRoomService chatRoomService;

  // 채팅방 생성
  @PostMapping("/{postId}")
  public ResponseEntity<?> createChatRoom(
      @AuthenticationPrincipal JwtUserDetails userDetails,
      @PathVariable Long postId,
      @RequestBody CreateChatRoomForm form
  ) {
    return ResponseEntity.ok(chatRoomService.createChatRoom(userDetails.getUsername(), postId, form.getName()));
  }

  // 참여중인 채팅방 목록 조회
  @GetMapping("/list")
  public ResponseEntity<?> getChatRoomList(@AuthenticationPrincipal JwtUserDetails userDetails) {
    return ResponseEntity.ok(chatRoomService.getChatRoomList(userDetails.getUsername()));
  }

  // 채팅방 입장
  @PostMapping("/{chatRoomId}")
  public ResponseEntity<?> enterChatRoom(
      @AuthenticationPrincipal JwtUserDetails userDetails,
      @PathVariable Long chatRoomId
  ) {
    return ResponseEntity.ok(chatRoomService.enterChatRoom(userDetails.getUsername(), chatRoomId));
  }

  // 채팅방 퇴장
  @DeleteMapping("/{chatRoomId}")
  public ResponseEntity<?> exitChatRoom(
      @AuthenticationPrincipal JwtUserDetails userDetails,
      @PathVariable Long chatRoomId
  ) {
    return ResponseEntity.ok(chatRoomService.exitChatRoom(userDetails.getUsername(), chatRoomId));
  }
}
