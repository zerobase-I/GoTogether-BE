package com.example.gotogetherbe.member.entitiy;

import com.example.gotogetherbe.accompany.review.entity.TravelScore;
import com.example.gotogetherbe.global.entity.BaseEntity;
import com.example.gotogetherbe.member.entitiy.type.MemberLoginType;
import com.example.gotogetherbe.member.entitiy.type.MemberGender;
import com.example.gotogetherbe.member.entitiy.type.MemberMbti;
import com.example.gotogetherbe.member.entitiy.type.MemberRoleType;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "travel_score_id")
  private TravelScore travelScore;

  @Builder.Default
  private Boolean certificationMark = false;

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

  @Builder.Default
  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts = new ArrayList<>();

  public void changeEmailAuth() {
    this.emailAuth = true;
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
