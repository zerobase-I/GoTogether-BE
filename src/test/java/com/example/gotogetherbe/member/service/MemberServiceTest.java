package com.example.gotogetherbe.member.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.global.util.aws.service.AwsS3Service;
import com.example.gotogetherbe.member.dto.MemberRequest;
import com.example.gotogetherbe.member.dto.MemberResponse;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.entitiy.type.MemberGender;
import com.example.gotogetherbe.member.entitiy.type.MemberRoleType;
import com.example.gotogetherbe.member.repository.MemberRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@Transactional
class MemberServiceTest {

  @InjectMocks
  private MemberService memberService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AwsS3Service awsS3Service;

  private Member member;

  @BeforeEach
  void setup() {
    member = Member.builder()
        .id(1L)
        .email("test@test.com")
        .password("12345")
        .nickname("김철수")
        .phoneNumber("010-1234-6789")
        .address("서울")
        .age(20)
        .gender(MemberGender.FEMALE)
        .roleType(MemberRoleType.USER)
        .build();
  }

  @Test
  @DisplayName("회원 정보 조회 성공")
  void success_GetMyProfileInfo() {
    // given
    given(memberRepository.findByEmail("test@test.com"))
        .willReturn(Optional.of(member));
    //when
    MemberResponse myProfileResponse = memberService.getMyProfileInfo("test@test.com");

    //then
    Assertions.assertEquals(myProfileResponse.getEmail(), member.getEmail());
  }

  @Test
  @DisplayName("회원 정보 조회 실패")
  void fail_GetMyProfileInfo() {
    //given
    given(memberRepository.findByEmail("test@test.com"))
        .willReturn(Optional.empty());
    //when
    GlobalException globalException = assertThrows(GlobalException.class,
        () -> memberService.getMyProfileInfo("test@test.com"));

    //then
    Assertions.assertEquals(ErrorCode.USER_NOT_FOUND, globalException.getErrorCode());
  }

  @Test
  @DisplayName("회원 정보 수정 성공 - 이미지 수정 X")
  void success_updateInfo() {
    //given
    MemberRequest updateRequest = MemberRequest.builder()
        .nickname("김민재")
        .phoneNumber("010-5555-6666")
        .password("1234512")
        .build();

    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));
    given(passwordEncoder.encode(updateRequest.getPassword())).willReturn("암호화된비밀번호");

    //when
    MemberResponse myProfileResponse = memberService.updateMyProfileInfo(updateRequest,
        "test@test.com", null);
    //then
    assertEquals(myProfileResponse.getNickname(), updateRequest.getNickname());
    assertEquals(myProfileResponse.getNickname(), updateRequest.getNickname());
    assertEquals(myProfileResponse.getPhoneNumber(), updateRequest.getPhoneNumber());
    assertEquals(myProfileResponse.getEmail(), member.getEmail());

    verify(memberRepository, times(1)).findByEmail("test@test.com");
    verify(passwordEncoder, times(1)).encode("1234512");
    verify(awsS3Service, never()).deleteFileUsingUrl(anyString()); // 이미지 삭제 메소드가 호출되지 않았음을 검증
    verify(awsS3Service, never()).uploadToS3(any(),any()); // 이미지 업로드 메소드가 호출되지 않았음을 검증
  }

  // 예를 들어, member 객체의 nickname, phoneNumber, password가 updateRequest의 값과 일치하는지 확인하는 등의 검증이 필요할 수 있습니다.

}