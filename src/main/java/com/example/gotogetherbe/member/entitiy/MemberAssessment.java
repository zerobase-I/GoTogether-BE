package com.example.gotogetherbe.member.entitiy;

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
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  private Long totalReviewCount;

  private Double rating;

  private Long punctualityCount;

  private Long responsivenessCount;

  private Long photographyCount;

  private Long mannerCount;

  private Long navigationCount;

  private Long humorCount;

  private Long adaptabilityCount;

  public void updateRatingAndTotalReviewCount(Double rating, Long totalReviewCount) {
    this.rating = rating;
    this.totalReviewCount = totalReviewCount;
  }

  public void updateAssessmentCount(boolean assessment, String assessmentType) {
    if (assessment) {
      switch (assessmentType) {
        case "punctuality":
          this.punctualityCount++;
          break;
        case "responsiveness":
          this.responsivenessCount++;
          break;
        case "photography":
          this.photographyCount++;
          break;
        case "manner":
          this.mannerCount++;
          break;
        case "navigation":
          this.navigationCount++;
          break;
        case "humor":
          this.humorCount++;
          break;
        case "adaptability":
          this.adaptabilityCount++;
          break;
      }
    }
  }
}