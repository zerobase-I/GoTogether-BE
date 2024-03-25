package com.example.gotogetherbe.member.entitiy;

import com.example.gotogetherbe.accompany.review.entity.MemberAssessment;
import com.example.gotogetherbe.global.entity.BaseEntity;
import com.example.gotogetherbe.member.entitiy.type.MemberGender;
import com.example.gotogetherbe.member.entitiy.type.MemberLoginType;
import com.example.gotogetherbe.member.entitiy.type.MemberMbti;
import com.example.gotogetherbe.member.entitiy.type.MemberRoleType;
import com.example.gotogetherbe.member.entitiy.type.MemberStatus;
import com.example.gotogetherbe.post.entity.Post;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostPersist;
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
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 30)
  private String email;

  private String password;

  @Column(nullable = false, length = 10)
  private String name;

  @Column(nullable = false, unique = true, length = 20)
  private String nickname;

  @Column(nullable = false, length = 50)
  private String address;

  @Column(nullable = false, length = 15)
  private String phoneNumber;

  @Column(nullable = false)
  private Integer age;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private MemberGender gender;

  @Column
  private String profileImageUrl;

  @Enumerated(EnumType.STRING)
  @Column(length = 10)
  private MemberMbti mbti;

  @Column(length = 20)
  private String instagramId;

  @Column
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private MemberLoginType loginType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private MemberRoleType roleType;

  @Builder.Default
  private Boolean alarmStatus = false;

  @Builder.Default
  private boolean emailAuth = false;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private MemberStatus status = MemberStatus.ACTIVE;

  @Builder.Default
  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts = new ArrayList<>();


  @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true,
      fetch = FetchType.LAZY)
  private MemberAssessment memberAssessment;
  @PostPersist
  public void initializeMemberAssessment() {
    this.memberAssessment = MemberAssessment.builder()
        .member(this)
        .totalReviewCount(0)
        .rating(0.0)
        .punctualityCount(0L)
        .responsivenessCount(0L)
        .photographyCount(0L)
        .mannerCount(0L)
        .navigationCount(0L)
        .humorCount(0L)
        .adaptabilityCount(0L)
        .build();
  }

  public void changeEmailAuth() {
    this.emailAuth = true;
  }

  public void changeAlarmStatus() {
    this.alarmStatus = !this.alarmStatus;
  }
  public void changeStatus(MemberStatus status) {
    this.status = status;
  }

  public void addPost(Post post){
    this.posts.add(post);
    post.setMember(this);
  }

  public void removePost(Post post){
    this.posts.remove(post);
    post.setMember(null);
  }

}
