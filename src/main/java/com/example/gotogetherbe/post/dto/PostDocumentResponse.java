package com.example.gotogetherbe.post.dto;


import com.example.gotogetherbe.post.entity.PostDocument;
import com.example.gotogetherbe.post.entity.type.PostCategory;
import com.example.gotogetherbe.post.entity.type.PostGenderType;
import com.example.gotogetherbe.post.entity.type.PostRecruitmentStatus;
import com.example.gotogetherbe.post.entity.type.TravelCityType;
import com.example.gotogetherbe.post.entity.type.TravelCountryType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PostDocumentResponse {

  private Long id;
  private Long memberId;
  private String userEmail;

  private PostRecruitmentStatus recruitmentStatus;

  private TravelCountryType travelCountry;

  private TravelCityType travelCity;


  private LocalDateTime startDate;
  private LocalDateTime endDate;


  private PostGenderType gender;

  private Integer minimumAge;

  private Integer maximumAge;

  private Integer recruitsPeople;

  private Integer currentPeople;

  private Integer estimatedTravelExpense;

  private PostCategory category;

  private String title;

  private String content;

  private LocalDateTime createdAt;

  private List<String> imagesUrl;

  public static PostDocumentResponse fromDocument(PostDocument postDocument) {
    return PostDocumentResponse.builder()
        .id(postDocument.getId())
        .memberId(postDocument.getMemberId())
        .userEmail(postDocument.getUserEmail())
        .recruitmentStatus(postDocument.getRecruitmentStatus())
        .travelCountry(postDocument.getTravelCountry())
        .travelCity(postDocument.getTravelCity())
        .startDate(postDocument.getStartDate())
        .endDate(postDocument.getEndDate())
        .gender(postDocument.getGender())
        .minimumAge(postDocument.getMinimumAge())
        .maximumAge(postDocument.getMaximumAge())
        .recruitsPeople(postDocument.getRecruitsPeople())
        .currentPeople(postDocument.getCurrentPeople())
        .estimatedTravelExpense(postDocument.getEstimatedTravelExpense())
        .category(postDocument.getCategory())
        .title(postDocument.getTitle())
        .content(postDocument.getContent())
        .createdAt(postDocument.getCreatedAt())
        .imagesUrl(postDocument.getImagesUrl())
        .build();
  }

}
