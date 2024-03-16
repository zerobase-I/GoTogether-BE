package com.example.gotogetherbe.member.entitiy.type;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberGender {

  MALE("male"),
  FEMALE("female");

  private final String code;
}
