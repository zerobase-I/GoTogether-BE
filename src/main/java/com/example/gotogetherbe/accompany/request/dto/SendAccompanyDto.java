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
public class SendAccompanyDto {

    private Long id;

    private Long requestMemberId;

    private Long requestedMemberId;

    private Long postId;

    private String postTitle;

    private AccompanyStatus requestStatus;

    private String createdAt;

    public static SendAccompanyDto from(Accompany accompanyRequest, String postTitle) {
        return SendAccompanyDto.builder()
            .id(accompanyRequest.getId())
            .requestMemberId(accompanyRequest.getRequestMember().getId())
            .requestedMemberId(accompanyRequest.getRequestedMember().getId())
            .postId(accompanyRequest.getPost().getId())
            .postTitle(postTitle)
            .requestStatus(accompanyRequest.getStatus())
            .createdAt(accompanyRequest.getCreatedAt().toString())
            .build();
    }
}
