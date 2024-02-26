package com.example.gotogetherbe.chatroom.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-room")
public class ChatRoomController {

  // 채팅방 생성
  @PostMapping("/{postId}")
  public ResponseEntity<?> createChatRoom() {
    return null;
  }

  // 참여중인 채팅방 목록 조회
  @GetMapping("/list")
  public ResponseEntity<?> getChatRoomList() {
    return null;
  }

  // 채팅방 입장
  @PostMapping("/{chatRoomId}")
  public ResponseEntity<?> enterChatRoom() {
    return null;
  }

  // 채팅방 퇴장
  @DeleteMapping("/{chatRoomId}")
  public ResponseEntity<?> exitChatRoom() {
    return null;
  }
}
