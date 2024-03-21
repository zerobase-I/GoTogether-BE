package com.example.gotogetherbe.post.entity;

import static org.springframework.data.elasticsearch.annotations.DateFormat.date_hour_minute_second_millis;
import static org.springframework.data.elasticsearch.annotations.DateFormat.epoch_millis;
import static org.springframework.data.elasticsearch.annotations.DateFormat.year_month_day;

import com.example.gotogetherbe.global.util.aws.entity.PostImage;
import com.example.gotogetherbe.post.entity.type.PostCategory;
import com.example.gotogetherbe.post.entity.type.PostGenderType;
import com.example.gotogetherbe.post.entity.type.PostRecruitmentStatus;
import com.example.gotogetherbe.post.entity.type.TravelCityType;
import com.example.gotogetherbe.post.entity.type.TravelCountryType;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostDocument{

  @Id
  private Long id;

  private Long memberId;

  private String userEmail;

  @Field(type = FieldType.Keyword)
  private PostRecruitmentStatus recruitmentStatus;

  @Field(type = FieldType.Keyword)
  private TravelCountryType travelCountry;

  @Field(type = FieldType.Keyword)
  private TravelCityType travelCity;


  @Field(type = FieldType.Date, format = {date_hour_minute_second_millis, epoch_millis, year_month_day})
  private LocalDateTime startDate;

  @Field(type = FieldType.Date, format = {date_hour_minute_second_millis, epoch_millis, year_month_day})
  private LocalDateTime endDate;


  @Field(type = FieldType.Keyword)
  private PostGenderType gender;

  @Field(type = FieldType.Integer)
  private Integer minimumAge;

  @Field(type = FieldType.Integer)
  private Integer maximumAge;

  @Field(type = FieldType.Integer)
  private Integer recruitsPeople;

  @Field(type = FieldType.Integer)
  private Integer currentPeople;

  @Field(type = FieldType.Integer)
  private Integer estimatedTravelExpense;

  @Field(type = FieldType.Keyword)
  private PostCategory category;

  @Field(type = FieldType.Text)
  private String title;

  @Field(type = FieldType.Text)
  private String content;

  @Field(type = FieldType.Date, format = {date_hour_minute_second_millis, epoch_millis})
  private LocalDateTime createdAt;

  @Field(type = FieldType.Date, format = {date_hour_minute_second_millis, epoch_millis})
  private LocalDateTime updatedAt;

  private List<String> imagesUrl;


  public static PostDocument from(Post post){
    List<String> imageUrlList = post.getImages().stream()
        .map(PostImage::getUrl).toList();

    return PostDocument.builder()
        .id(post.getId())
        .memberId(post.getMember().getId())
        .userEmail(post.getMember().getEmail())
        .recruitmentStatus(post.getRecruitmentStatus())
        .travelCountry(post.getTravelCountry())
        .travelCity(post.getTravelCity())
        .startDate(post.getStartDate())
        .endDate(post.getEndDate())
        .gender(post.getGender())
        .minimumAge(post.getMinimumAge())
        .maximumAge(post.getMaximumAge())
        .recruitsPeople(post.getRecruitsPeople())
        .currentPeople(post.getCurrentPeople())
        .estimatedTravelExpense(post.getEstimatedTravelExpense())
        .category(post.getCategory())
        .title(post.getTitle())
        .content(post.getContent())
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getUpdatedAt())
        .imagesUrl(imageUrlList)
        .build();
  }
}
