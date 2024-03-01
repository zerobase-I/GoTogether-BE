package com.example.gotogetherbe.accompany.review.dto;

import com.example.gotogetherbe.accompany.review.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewWriteDto {

    private Long reviewerId;
    private Long targetMemberId;
    private Long postId;
    private Double score;
    private String content;

    public Review toEntity() {
        return Review.builder()
            .reviewerId(reviewerId)
            .targetMemberId(targetMemberId)
            .postId(postId)
            .score(score)
            .content(content)
            .build();
    }

}
