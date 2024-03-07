package com.example.gotogetherbe.chat.repository;

public interface CustomChatMemberRepository {
    boolean areUsersInSameChatRoom(Long reviewerId, Long targetMemberId, Long chatRoomId);

}
