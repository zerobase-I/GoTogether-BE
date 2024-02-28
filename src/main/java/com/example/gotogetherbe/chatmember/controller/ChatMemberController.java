package com.example.gotogetherbe.chatmember.controller;

import com.example.gotogetherbe.chatmember.service.impl.ChatMemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMemberController {
  private final ChatMemberServiceImpl chatMemberService;

  // 채팅방 참여자 목록 조회
  @GetMapping("/api/chat-member/list/{chatRoomId}")
  public ResponseEntity<?> getChatMemberList(
      /* 회원인증 */
      @PathVariable Long chatRoomId
  ) {
    return ResponseEntity.ok(chatMemberService.getChatMemberList(chatRoomId));
  }
}
