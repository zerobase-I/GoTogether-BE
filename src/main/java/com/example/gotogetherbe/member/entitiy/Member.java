package com.example.gotogetherbe.member.entitiy;

import com.example.gotogetherbe.global.entity.BaseEntity;
import com.example.gotogetherbe.member.entitiy.type.MemberLoginType;
import com.example.gotogetherbe.member.entitiy.type.MemberGender;
import com.example.gotogetherbe.member.entitiy.type.MemberMbti;
import com.example.gotogetherbe.member.entitiy.type.MemberRoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

  @Column(nullable = false)
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

  @Column(nullable = false)
  private Integer travelScore;

  @Column(nullable = false)
  private Boolean certificationMark;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private MemberLoginType loginType;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private MemberRoleType roleType;

  @Column(nullable = false)
  private Boolean alarmStatus;

  @Builder.Default
  private boolean emailAuth = false;

  public void changeEmailAuth() {
    this.emailAuth = true;
  }

}
