package com.example.gotogetherbe.chat.dto;

import com.example.gotogetherbe.chat.entity.ChatRoom;
import com.example.gotogetherbe.chat.type.ChatRoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRoomDto {
  private Long chatRoomId;
  private Long postId;
  private String name;
  private ChatRoomStatus status;

  public static ChatRoomDto from(ChatRoom chatRoom) {
    return ChatRoomDto.builder()
        .chatRoomId(chatRoom.getId())
        .postId(chatRoom.getPost().getId())
        .name(chatRoom.getName())
        .status(chatRoom.getStatus())
        .build();
  }
}
