package com.example.gotogetherbe.accompany.review.dto;

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

    private Double score;

    private String content;

}
