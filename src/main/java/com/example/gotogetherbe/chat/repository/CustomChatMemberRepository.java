package com.example.gotogetherbe.chat.repository;

public interface CustomChatMemberRepository {

    boolean isUsersInSameChatRoom(Long reviewerId, Long targetMemberId, Long chatRoomId);

}
