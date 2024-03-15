package com.example.gotogetherbe.mainschedule;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.mainschedule.dto.MainScheduleDto;
import com.example.gotogetherbe.mainschedule.dto.MainScheduleRequest;
import com.example.gotogetherbe.mainschedule.entity.MainSchedule;
import com.example.gotogetherbe.mainschedule.repository.MainScheduleRepository;
import com.example.gotogetherbe.mainschedule.service.MainScheduleService;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.entitiy.type.MemberGender;
import com.example.gotogetherbe.member.entitiy.type.MemberLoginType;
import com.example.gotogetherbe.member.entitiy.type.MemberMbti;
import com.example.gotogetherbe.member.entitiy.type.MemberRoleType;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.entity.type.PostCategory;
import com.example.gotogetherbe.post.entity.type.PostGenderType;
import com.example.gotogetherbe.post.entity.type.TravelCityType;
import com.example.gotogetherbe.post.entity.type.TravelCountryType;
import com.example.gotogetherbe.post.repository.PostRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Transactional
public class MainScheduleServiceTest {

  @InjectMocks
  MainScheduleService mainScheduleService;

  @Mock
  MainScheduleRepository mainScheduleRepository;

  @Mock
  MemberRepository memberRepository;

  @Mock
  PostRepository postRepository;

  private Member member;
  private Post post;

  @BeforeEach
  void setUp() {
    member = Member.builder()
        .id(1L)
        .email("test1234@gmail.com")
        .password("1234")
        .name("kim")
        .nickname("nick")
        .address("서울시 강남구")
        .phoneNumber("010-1234-5678")
        .age(25)
        .gender(MemberGender.MALE)
        .profileImageUrl(null)
        .mbti(MemberMbti.ISFP)
        .instagramId("instagram123")
        .description("hi i'm kim")
        .loginType(MemberLoginType.EMAIL)
        .roleType(MemberRoleType.USER)
        .emailAuth(true)
        .posts(null)
        .build();

    post = Post.builder()
        .id(1L)
        .member(member)
        .travelCountry(TravelCountryType.KOREA)
        .travelCity(TravelCityType.SEOUL)
        .startDate(LocalDateTime.parse("2024-05-01T12:00:00"))
        .endDate(LocalDateTime.parse("2024-05-03T12:00:00"))
        .gender(PostGenderType.ALL)
        .minimumAge(20)
        .maximumAge(30)
        .recruitsPeople(5)
        .estimatedTravelExpense(500_000)
        .category(PostCategory.FOOD_CAFE)
        .title("Go Seoul")
        .content("My First Trip At Seoul")
        .chatRoomExists(false)
        .build();
  }

  @Test
  @DisplayName("주요 일정 생성 성공")
  void createMainSchedule_Success() {
    //given
    MainSchedule mainSchedule = MainSchedule.builder()
        .id(1L)
        .post(post)
        .scheduleDate(LocalDate.parse("2024-05-01"))
        .content("CAFE")
        .build();

    MainScheduleRequest request = new MainScheduleRequest(LocalDate.parse("2024-05-01"), "CAFE");

    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));

    given(mainScheduleRepository.save(any()))
        .willReturn(mainSchedule);

    //when
    MainScheduleDto mainScheduleDto = mainScheduleService.createMainSchedule(member.getEmail(), post.getId(), request);

    //then
    assertThat(mainScheduleDto.getContent()).isEqualTo("CAFE");
    assertThat(mainScheduleDto.getScheduleDate()).isEqualTo(LocalDate.parse("2024-05-01"));
  }

  @Test
  @DisplayName("주요 일정 생성 실패 : 회원의 게시글이 아닐 경우")
  void createMainSchedule_FailMemberPostIncorrect() {
    //given
    MainScheduleRequest request = new MainScheduleRequest();

    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    Member other = Member.builder().id(2L).build();
    post.setMember(other);
    given(postRepository.findById(anyLong()))
        .willReturn(Optional.of(post));



    //when
    GlobalException globalException = Assertions.assertThrows(GlobalException.class,
        () -> mainScheduleService.createMainSchedule(member.getEmail(), post.getId(), request));

    //then
    assertThat(ErrorCode.MEMBER_POST_INCORRECT.getDescription()).isEqualTo(globalException.getErrorCode().getDescription());
  }

  @Test
  @DisplayName("주요 일정 목록 조회 성공")
  void getMainSchedule_Success() {
    //given
    MainSchedule mainSchedule = MainSchedule.builder()
        .id(1L)
        .post(post)
        .content("cafe")
        .scheduleDate(LocalDate.parse("2024-05-01"))
        .build();

    given(mainScheduleRepository.findAllByPostId(anyLong()))
        .willReturn(List.of(mainSchedule));

    //when
    List<MainScheduleDto> mainScheduleDtoList = mainScheduleService.getMainSchedule(post.getId());

    //then
    assertFalse(mainScheduleDtoList.isEmpty());
  }

  @Test
  @DisplayName("주요 일정 수정 성공")
  void updateMainSchedule_Success() {
    //given
    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    MainSchedule mainSchedule = MainSchedule.builder()
        .id(1L)
        .post(post)
        .content("cafe")
        .scheduleDate(LocalDate.parse("2024-05-01"))
        .build();

    given(mainScheduleRepository.findById(anyLong()))
        .willReturn(Optional.of(mainSchedule));

    MainScheduleRequest request = MainScheduleRequest.builder()
        .content("food")
        .scheduleDate(LocalDate.parse("2024-05-02"))
        .build();

    mainSchedule.updateContent(request.getContent());
    mainSchedule.updateScheduleDate(request.getScheduleDate());

    given(mainScheduleRepository.save(any()))
        .willReturn(mainSchedule);

    //when
    MainScheduleDto mainScheduleDto = mainScheduleService.updateMainSchedule(member.getEmail(), 1L, request);

    //then
    assertThat("food").isEqualTo(mainScheduleDto.getContent());
    assertThat(LocalDate.parse("2024-05-02")).isEqualTo(mainScheduleDto.getScheduleDate());

  }

  @Test
  @DisplayName("주요 일정 수정 실패 : 회원 본인이 작성한 주요 일정이 아닐경우")
  void updateMainSchedule_Fail() {
    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    MainSchedule mainSchedule = MainSchedule.builder()
        .id(1L)
        .post(post)
        .content("cafe")
        .scheduleDate(LocalDate.parse("2024-05-01"))
        .build();

    Member other = Member.builder().id(2L).build();
    post.setMember(other);
    mainSchedule.setPost(post);

    given(mainScheduleRepository.findById(anyLong()))
        .willReturn(Optional.of(mainSchedule));

    MainScheduleRequest request = MainScheduleRequest.builder().build();

    //when
    GlobalException globalException = Assertions.assertThrows(GlobalException.class,
        () -> mainScheduleService.updateMainSchedule(member.getEmail(), 1L, request));

    //then
    assertThat(ErrorCode.MEMBER_AND_MAIN_SCHEDULE_INCORRECT.getDescription()).isEqualTo(globalException.getErrorCode().getDescription());
  }

  @Test
  @DisplayName("주요 일정 삭제 성공")
  void deleteMainSchedule_Success() {
    //given
    given(memberRepository.findByEmail(anyString()))
        .willReturn(Optional.of(member));

    MainSchedule mainSchedule = MainSchedule.builder()
        .id(1L)
        .post(post)
        .content("cafe")
        .scheduleDate(LocalDate.parse("2024-05-01"))
        .build();

    given(mainScheduleRepository.findById(anyLong()))
        .willReturn(Optional.of(mainSchedule));

    //when
    MainScheduleDto mainScheduleDto = mainScheduleService.deleteMainSchedule(member.getEmail(), 1L);

    //then
    assertThat(1L).isEqualTo(mainScheduleDto.getId());
  }
}
