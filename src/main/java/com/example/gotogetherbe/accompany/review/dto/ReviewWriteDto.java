package com.example.gotogetherbe.accompany.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewWriteDto {

    private Long targetMemberId;

    private Long postId;

    @Max(5)
    @Min(0)
    private Double score;

    private boolean punctuality;

    private boolean responsiveness;

    private boolean photography;

    private boolean manner;

    private boolean navigation;

    private boolean humor;

    private boolean adaptability;

}
