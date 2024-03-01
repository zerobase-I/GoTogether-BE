package com.example.gotogetherbe.chat.controller;

import com.example.gotogetherbe.chat.service.impl.ChatMemberServiceImpl;
import com.example.gotogetherbe.global.util.jwt.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
      @AuthenticationPrincipal JwtUserDetails userDetails,
      @PathVariable Long chatRoomId
  ) {
    return ResponseEntity.ok(chatMemberService.getChatMemberList(userDetails.getUsername(),chatRoomId));
  }
}
