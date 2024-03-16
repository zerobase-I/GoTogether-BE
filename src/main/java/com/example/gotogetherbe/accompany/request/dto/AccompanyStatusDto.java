package com.example.gotogetherbe.accompany.request.dto;

import com.example.gotogetherbe.accompany.request.entity.Accompany;
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

    private String requestStatus;

    private String createdAt;

    public static AccompanyStatusDto from(Accompany accompanyRequest) {
        return AccompanyStatusDto.builder()
            .id(accompanyRequest.getId())
            .requestMemberId(accompanyRequest.getRequestMember().getId())
            .requestedMemberId(accompanyRequest.getRequestedMember().getId())
            .postId(accompanyRequest.getPost().getId())
            .requestStatus(accompanyRequest.getStatus().name())
            .createdAt(accompanyRequest.getCreatedAt().toString())
            .build();
    }
}
