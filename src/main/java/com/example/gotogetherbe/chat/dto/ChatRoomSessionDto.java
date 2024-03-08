package com.example.gotogetherbe.chat.dto;

import lombok.Builder;

@Builder
public record ChatRoomSessionDto(Long memberId, Long chatRoomId) {

}
