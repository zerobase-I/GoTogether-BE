package com.example.gotogetherbe.chatroom.service;

import com.example.gotogetherbe.chatroom.dto.ChatRoomDto;
import java.util.List;

public interface ChatRoomService {

  public ChatRoomDto createChatRoom(String email, Long postId, String name);

  public List<ChatRoomDto> getChatRoomList(String email);

  public String enterChatRoom(String email, Long chatRoomId);

  public String exitChatRoom(String email, Long chatRoomId);
}
