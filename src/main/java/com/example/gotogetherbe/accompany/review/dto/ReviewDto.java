package com.example.gotogetherbe.accompany.review.dto;

import com.example.gotogetherbe.accompany.review.entity.Review;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {

    private Long id;

    private Long reviewerId;

    private Long targetMemberId;

    private Long postId;

    private Double score;

    private String content;

    private LocalDateTime createdAt;

    public static ReviewDto from(Review review) {
        return ReviewDto.builder()
            .id(review.getId())
            .reviewerId(review.getReviewer().getId())
            .targetMemberId(review.getTargetMember().getId())
            .postId(review.getPost().getId())
            .score(review.getScore())
            .content(review.getContent())
            .createdAt(review.getCreatedAt())
            .build();
    }

}
