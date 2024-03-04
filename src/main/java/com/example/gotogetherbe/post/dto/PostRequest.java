package com.example.gotogetherbe.post.dto;

import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.entity.type.PostCategory;
import com.example.gotogetherbe.post.entity.type.PostGenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostRequest {
  @NotBlank(message = "여행국가를 입력해주세요.")
  private String travelCountry;
  @NotBlank(message = "여행도시를 입력해주세요.")
  private String travelCity;

  @NotNull(message = "선호성별을 선택해주세요.")
  private PostGenderType postGenderType;
  @NotNull(message = "카테고리를 선택해주세요.")
  private PostCategory postCategory;
  @NotNull(message = "모집인원을 입력해주세요.")
  private Integer recruitsPeople;
  @NotNull(message = "예상여행경비를 입력해주세요.")
  private Integer estimatedTravelExpense;
  @NotNull(message = "최소나이를 입력해주세요.")
  private Integer minimumAge;
  @NotNull(message = "최대나이를 입력해주세요.")
  private Integer maximumAge;
  @NotNull(message = "시작날짜를 입력해주세요.")
  private LocalDateTime startDate;
  @NotNull(message = "종료날짜를 입력해주세요.")
  private LocalDateTime endDate;

  @NotBlank(message = "제목을 입력해주세요.")
  private String title;
  @NotBlank(message = "내용을 입력해주세요.")
  private String content;

  public Post toEntity(){
    return Post.builder()
        .travelCountry(travelCountry)
        .travelCity(travelCity)
        .gender(postGenderType)
        .category(postCategory)
        .recruitsPeople(recruitsPeople)
        .estimatedTravelExpense(estimatedTravelExpense)
        .minimumAge(minimumAge)
        .maximumAge(maximumAge)
        .startDate(startDate)
        .endDate(endDate)
        .title(title)
        .content(content)
        .build();
  }
}
