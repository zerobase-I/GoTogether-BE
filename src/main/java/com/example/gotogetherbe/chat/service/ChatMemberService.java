package com.example.gotogetherbe.chat.service;

import com.example.gotogetherbe.chat.dto.ChatMemberDto;
import java.util.List;

public interface ChatMemberService {

  // 채팅방 참여자 목록 조회
  public List<ChatMemberDto> getChatMemberList(String email, Long chatRoomId);
}
