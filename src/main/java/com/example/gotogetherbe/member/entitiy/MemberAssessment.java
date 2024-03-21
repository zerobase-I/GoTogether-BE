package com.example.gotogetherbe.member.entitiy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberAssessment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @Column
  private Integer totalReviewCount;

  @Column
  @Min(0)
  @Max(5)
  private Double rating;

  @Column
  private Long punctualityCount;

  @Column
  private Long responsivenessCount;

  @Column
  private Long photographyCount;

  @Column
  private Long mannerCount;

  @Column
  private Long navigationCount;

  @Column
  private Long humorCount;

  @Column
  private Long adaptabilityCount;

}
