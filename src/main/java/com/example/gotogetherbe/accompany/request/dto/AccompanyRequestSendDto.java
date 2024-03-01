package com.example.gotogetherbe.accompany.request.dto;

import com.example.gotogetherbe.accompany.request.entity.AccompanyRequest;
import com.example.gotogetherbe.accompany.request.type.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccompanyRequestSendDto {

    private Long requestMemberId;

    private Long requestedMemberId;

    private Long postId;

    public AccompanyRequest toEntity() {
        return AccompanyRequest.builder()
                .requestMemberId(requestMemberId)
                .requestedMemberId(requestedMemberId)
                .postId(postId)
                .requestStatus(RequestStatus.WAITING)
                .build();
    }
}
