package com.example.gotogetherbe.accompany.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccompanyRequestSendDto {

    private Long requestedMemberId;

    private Long postId;

}
