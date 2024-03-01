package com.example.gotogetherbe.accompany.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestConfirmDto {

    private Long memberId;

    private Long requestId;

}
