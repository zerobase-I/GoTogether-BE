package com.example.gotogetherbe.post.dto;

import com.example.gotogetherbe.global.util.aws.dto.S3ImageDto;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.entity.type.PostCategory;
import com.example.gotogetherbe.post.entity.type.PostGenderType;
import com.example.gotogetherbe.post.entity.type.PostRecruitmentStatus;
import com.example.gotogetherbe.post.entity.type.TravelCityType;
import com.example.gotogetherbe.post.entity.type.TravelCountryType;
import java.time.LocalDateTime;
import java.util.List;
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
  private Long memberId;
  private String userEmail;

  private TravelCountryType travelCountry;
  private TravelCityType travelCity;

  private PostRecruitmentStatus recruitmentStatus;
  private PostGenderType postGenderType;
  private PostCategory postCategory;

  private Integer recruitsPeople;
  private Integer estimatedTravelExpense;
  private Integer minimumAge;
  private Integer maximumAge;
  private Integer currentPeople;

  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private LocalDateTime createdAt;

  private String title;
  private String content;
  private List<S3ImageDto> images;

  public static PostResponse fromEntity(Post post){
    List<S3ImageDto> imageList = post.getImages().stream()
        .map(S3ImageDto::fromEntity).toList();

    return PostResponse.builder()
        .id(post.getId())
        .memberId(post.getMember().getId())
        .userEmail(post.getMember().getEmail())
        .travelCountry(post.getTravelCountry())
        .travelCity(post.getTravelCity())
        .recruitmentStatus(post.getRecruitmentStatus())
        .postGenderType(post.getGender())
        .postCategory(post.getCategory())
        .recruitsPeople(post.getRecruitsPeople())
        .estimatedTravelExpense(post.getEstimatedTravelExpense())
        .minimumAge(post.getMinimumAge())
        .maximumAge(post.getMaximumAge())
        .currentPeople(post.getCurrentPeople())
        .startDate(post.getStartDate())
        .endDate(post.getEndDate())
        .createdAt(post.getCreatedAt())
        .title(post.getTitle())
        .content(post.getContent())
        .images(imageList)
        .build();
  }
}
