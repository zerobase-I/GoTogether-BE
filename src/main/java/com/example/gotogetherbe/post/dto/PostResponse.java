package com.example.gotogetherbe.post.dto;

import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.entity.type.PostCategory;
import com.example.gotogetherbe.post.entity.type.PostGenderType;
import com.example.gotogetherbe.post.entity.type.PostRecruitmentStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
  private Long id;
  private String userEmail;

  private String travelCountry;
  private String travelCity;

  private PostRecruitmentStatus recruitmentStatus;
  private PostGenderType postGenderType;
  private PostCategory postCategory;
  private Integer recruitsPeople;
  private Integer estimatedTravelExpense;
  private Integer minimumAge;
  private Integer maximumAge;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private LocalDateTime createdAt;

  private String title;
  private String content;

  public static PostResponse fromEntity(Post post){
    return PostResponse.builder()
        .id(post.getId())
        .userEmail(post.getMember().getEmail())
        .travelCountry(post.getTravelCountry())
        .travelCity(post.getTravelCity())
        .recruitmentStatus(PostRecruitmentStatus.RECRUITING)
        .postGenderType(post.getGender())
        .postCategory(post.getCategory())
        .recruitsPeople(post.getRecruitsPeople())
        .estimatedTravelExpense(post.getEstimatedTravelExpense())
        .minimumAge(post.getMinimumAge())
        .maximumAge(post.getMaximumAge())
        .startDate(post.getStartDate())
        .endDate(post.getEndDate())
        .createdAt(post.getCreatedAt())
        .title(post.getTitle())
        .content(post.getContent())
        .build();
  }
}
