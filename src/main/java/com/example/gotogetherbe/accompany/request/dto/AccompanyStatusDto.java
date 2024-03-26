package com.example.gotogetherbe.accompany.request.dto;

import com.example.gotogetherbe.accompany.request.entity.Accompany;
import com.example.gotogetherbe.accompany.request.type.AccompanyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccompanyStatusDto {

    private Long id;

    private Long requestMemberId;

    private Long requestedMemberId;

    private Long postId;

    private Long chatRoomId;

    private AccompanyStatus requestStatus;

    private String createdAt;

    public static AccompanyStatusDto from(Accompany accompanyRequest, Long chatRoomId) {
        return AccompanyStatusDto.builder()
            .id(accompanyRequest.getId())
            .requestMemberId(accompanyRequest.getRequestMember().getId())
            .requestedMemberId(accompanyRequest.getRequestedMember().getId())
            .postId(accompanyRequest.getPost().getId())
            .chatRoomId(chatRoomId)
            .requestStatus(accompanyRequest.getStatus())
            .createdAt(accompanyRequest.getCreatedAt().toString())
            .build();
    }
}
