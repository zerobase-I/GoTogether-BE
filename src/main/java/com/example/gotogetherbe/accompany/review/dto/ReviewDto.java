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

    private boolean punctuality;

    private boolean responsiveness;

    private boolean photography;

    private boolean manner;

    private boolean navigation;

    private boolean humor;

    private boolean adaptability;

    private LocalDateTime createdAt;

    public static ReviewDto from(Review review) {
        return ReviewDto.builder()
            .id(review.getId())
            .reviewerId(review.getReviewer().getId())
            .targetMemberId(review.getTargetMember().getId())
            .postId(review.getPost().getId())
            .score(review.getScore())
            .punctuality(review.isPunctuality())
            .responsiveness(review.isResponsiveness())
            .photography(review.isPhotography())
            .manner(review.isManner())
            .navigation(review.isNavigation())
            .humor(review.isHumor())
            .adaptability(review.isAdaptability())
            .createdAt(review.getCreatedAt())
            .build();
    }

}
