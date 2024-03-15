package com.example.gotogetherbe.post.entity;

import com.example.gotogetherbe.global.entity.BaseEntity;
import com.example.gotogetherbe.global.util.aws.entity.PostImage;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.post.entity.type.PostCategory;
import com.example.gotogetherbe.post.entity.type.PostGenderType;
import com.example.gotogetherbe.post.entity.type.PostRecruitmentStatus;
import com.example.gotogetherbe.post.entity.type.TravelCityType;
import com.example.gotogetherbe.post.entity.type.TravelCountryType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PostRecruitmentStatus recruitmentStatus = PostRecruitmentStatus.RECRUITING;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TravelCountryType travelCountry;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TravelCityType travelCity;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PostGenderType gender;

    @Column(nullable = false)
    private Integer minimumAge;

    @Column(nullable = false)
    private Integer maximumAge;

    @Column(nullable = false)
    private Integer recruitsPeople;

    @Builder.Default
    @Column(nullable = false)
    private Integer currentPeople = 0;

    @Column
    private Integer estimatedTravelExpense;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private PostCategory category;

    @Column(nullable = false, length = 20)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    @Builder.Default
    Boolean chatRoomExists = false;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    public void addImage(PostImage image) {
        this.images.add(image);
        image.mappingPost(this);
    }

    public void removeImage(PostImage image) {
        this.images.remove(image);
    }

    public void updateCurrentPeople() {
        this.currentPeople++;
    }

}
