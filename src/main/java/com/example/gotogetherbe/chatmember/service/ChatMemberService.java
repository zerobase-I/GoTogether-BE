package com.example.gotogetherbe.chatmember.service;

import com.example.gotogetherbe.chatmember.dto.ChatMemberDto;
import java.util.List;

public interface ChatMemberService {

  // 채팅방 참여자 목록 조회
  public List<ChatMemberDto> getChatMemberList(Long chatRoomId);
}
