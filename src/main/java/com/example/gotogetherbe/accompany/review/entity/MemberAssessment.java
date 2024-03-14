package com.example.gotogetherbe.accompany.review.entity;

import com.example.gotogetherbe.member.entitiy.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id", nullable = true)
    private Member member;

    private Integer totalReviewCount;

    private Double rating;

    private Long punctualityCount;

    private Long responsivenessCount;

    private Long photographyCount;

    private Long mannerCount;

    private Long navigationCount;

    private Long humorCount;

    private Long adaptabilityCount;

    public void updateRatingAndTotalReviewCount(Double rating, Integer totalReviewCount) {
        this.rating = rating;
        this.totalReviewCount = totalReviewCount;
    }

    public void updateAssessmentCount(boolean assessment) {
        if (assessment) {
            this.punctualityCount++;
        }
    }
}
