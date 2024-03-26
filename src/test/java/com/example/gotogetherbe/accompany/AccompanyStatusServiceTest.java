package com.example.gotogetherbe.accompany;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.gotogetherbe.accompany.request.dto.AccompanyStatusDto;
import com.example.gotogetherbe.accompany.request.dto.ReceiveAccompanyDto;
import com.example.gotogetherbe.accompany.request.dto.SendAccompanyDto;
import com.example.gotogetherbe.accompany.request.entity.Accompany;
import com.example.gotogetherbe.accompany.request.repository.AccompanyRepository;
import com.example.gotogetherbe.accompany.request.service.AccompanyStatusService;
import com.example.gotogetherbe.accompany.request.type.AccompanyStatus;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.entitiy.type.MemberGender;
import com.example.gotogetherbe.member.entitiy.type.MemberLoginType;
import com.example.gotogetherbe.member.entitiy.type.MemberMbti;
import com.example.gotogetherbe.member.entitiy.type.MemberRoleType;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.entity.type.PostCategory;
import com.example.gotogetherbe.post.entity.type.PostGenderType;
import com.example.gotogetherbe.post.entity.type.PostRecruitmentStatus;
import com.example.gotogetherbe.post.entity.type.TravelCityType;
import com.example.gotogetherbe.post.entity.type.TravelCountryType;
import com.example.gotogetherbe.post.repository.PostRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AccompanyStatusServiceTest {

    @InjectMocks
    private AccompanyStatusService accompanyStatusService;

    @Mock
    private AccompanyRepository accompanyRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostRepository postRepository;

    private Member member1;
    private Member member2;
    private Post post;
    private Accompany accompany;

    @BeforeEach
    void setup() {
        post = Post.builder()
            .id(1L)
            .member(member2)
            .travelCountry(TravelCountryType.KOREA)
            .travelCity(TravelCityType.SEOUL)
            .startDate(LocalDateTime.parse("2024-03-22T12:00:00"))
            .endDate(LocalDateTime.parse("2024-03-29T12:00:00"))
            .gender(PostGenderType.ALL)
            .minimumAge(20)
            .maximumAge(30)
            .recruitsPeople(5)
            .estimatedTravelExpense(100_000)
            .recruitmentStatus(PostRecruitmentStatus.RECRUITING)
            .category(PostCategory.ACTIVITY_TOUR)
            .title("Seoul Trip")
            .content("Let's go to Seoul!")
            .chatRoomExists(false)
            .currentPeople(1)
            .build();

        member1 = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .password("1234")
            .name("김철수")
            .nickname("김철수")
            .address("서울시")
            .phoneNumber("010-1234-5678")
            .age(20)
            .gender(MemberGender.MALE)
            .profileImageUrl(null)
            .mbti(MemberMbti.INFP)
            .instagramId("instagram999")
            .description("hi i'm kim")
            .loginType(MemberLoginType.EMAIL)
            .roleType(MemberRoleType.USER)
            .emailAuth(true)
            .posts(null)
            .build();

        member2 = Member.builder()
            .id(2L)
            .email("test222@gmail.com")
            .password("1234")
            .name("아무개")
            .nickname("아무개")
            .address("경기도")
            .phoneNumber("010-1234-5678")
            .age(25)
            .gender(MemberGender.FEMALE)
            .profileImageUrl(null)
            .mbti(MemberMbti.ISFP)
            .instagramId("instagram123")
            .description("hi i'm kim")
            .loginType(MemberLoginType.EMAIL)
            .roleType(MemberRoleType.USER)
            .emailAuth(true)
            .posts(List.of(post))
            .build();

        accompany = Accompany.builder()
            .id(1L)
            .requestMember(member1)
            .requestedMember(member2)
            .post(post)
            .status(AccompanyStatus.WAITING)
            .createdAt(LocalDateTime.now())
            .build();

    }

    @Test
    @DisplayName("동행 요청 보내기 성공")
    void sendAccompanyRequestSuccess() {
        // given
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member1));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(accompanyRepository.save(any())).willReturn(accompany);

        // when
        AccompanyStatusDto accompanyStatusDto = accompanyStatusService
            .sendAccompanyRequest(member1.getEmail(), post.getId());

        // then
        assertThat(accompanyStatusDto.getRequestStatus()).isEqualTo(accompany.getStatus());
        assertThat(accompanyStatusDto.getRequestMemberId()).isEqualTo(
            accompany.getRequestMember().getId());
        assertThat(accompanyStatusDto.getRequestedMemberId()).isEqualTo(
            accompany.getRequestedMember().getId());
        assertThat(accompanyStatusDto.getPostId()).isEqualTo(accompany.getPost().getId());
    }

    @Test
    @DisplayName("동행 요청 보내기 실패, 중복 예외")
    void sendAccompanyRequestFail() {
        // given
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member1));
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(accompanyRepository.existsByRequestMember_IdAndPost_Id(anyLong(), anyLong()))
            .willReturn(true);

        // when
        GlobalException globalException = assertThrows(GlobalException.class,
            () -> accompanyStatusService.sendAccompanyRequest(member1.getEmail(), post.getId()));

        // then
        Assertions.assertEquals(ErrorCode.DUPLICATE_ACCOMPANY_REQUEST,
            globalException.getErrorCode());
    }

    @Test
    @DisplayName("보낸 동행 요청 조회 성공")
    void getSentAccompanyRequestSuccess() {
        // given
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member1));
        given(accompanyRepository.findAllByRequestMemberIdAndStatusOrderByCreatedAtDesc(anyLong(),
            any())).willReturn(List.of(accompany));

        // when
        List<SendAccompanyDto> accompanyStatusDtoList = accompanyStatusService
            .getSentAccompanyRequests(member1.getEmail());

        // then
        assertThat(accompanyStatusDtoList.size()).isEqualTo(1);
        assertThat(accompanyStatusDtoList.get(0).getRequestMemberId()).isEqualTo(member1.getId());
        assertThat(accompanyStatusDtoList.get(0).getRequestStatus()).isEqualTo(
            accompany.getStatus());
    }

    @Test
    @DisplayName("받은 동행 요청 조회 성공")
    void getReceivedAccompanyRequestSuccess() {
        // given
        given(memberRepository.findByEmail(anyString())).willReturn(Optional.of(member2));
        given(accompanyRepository.findAllByRequestedMemberIdAndStatusOrderByCreatedAtDesc(anyLong(),
            any())).willReturn(List.of(accompany));

        // when
        List<ReceiveAccompanyDto> accompanyStatusDtoList = accompanyStatusService
            .getReceivedAccompanyRequests(member2.getEmail());

        // then
        assertThat(accompanyStatusDtoList.size()).isEqualTo(1);
        assertThat(accompanyStatusDtoList.get(0).getRequestedMemberId()).isEqualTo(member2.getId());
        assertThat(accompanyStatusDtoList.get(0).getRequestStatus()).isEqualTo(
            accompany.getStatus());
    }

    @Test
    @DisplayName("동행 요청 승인 성공")
    void approveAccompanyRequestSuccess() {
        // given
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));
        given(accompanyRepository.save(any())).willReturn(accompany);
        given(postRepository.findById(anyLong())).willReturn(Optional.of(post));
        given(postRepository.save(any())).willReturn(post);

        // when
        AccompanyStatusDto accompanyStatusDto = accompanyStatusService
            .approveAccompanyRequest(member2.getEmail(), accompany.getId());

        // then
        assertThat(accompanyStatusDto.getRequestStatus()).isEqualTo(AccompanyStatus.PARTICIPATING);
        assertThat(accompanyStatusDto.getRequestMemberId()).isEqualTo(
            accompany.getRequestMember().getId());
        assertThat(accompanyStatusDto.getRequestedMemberId()).isEqualTo(
            accompany.getRequestedMember().getId());
        assertThat(accompanyStatusDto.getPostId()).isEqualTo(accompany.getPost().getId());
        assertThat(post.getCurrentPeople()).isEqualTo(2);
    }

    @Test
    @DisplayName("동행 요청 승인 실패, 요청 ID 없음")
    void approveAccompanyRequestFail() {
        // given
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        GlobalException globalException = assertThrows(GlobalException.class,
            () -> accompanyStatusService.approveAccompanyRequest(member2.getEmail(), 2L));

        // then
        Assertions.assertEquals(ErrorCode.ACCOMPANY_REQUEST_NOT_FOUND,
            globalException.getErrorCode());
    }

    @Test
    @DisplayName("동행 요청 승인 실패, 사용자 불일치")
    void approveAccompanyRequestFail2() {
        // given
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));

        // when
        GlobalException globalException = assertThrows(GlobalException.class,
            () -> accompanyStatusService.approveAccompanyRequest(member1.getEmail(),
                accompany.getId()));

        // then
        Assertions.assertEquals(ErrorCode.USER_MISMATCH, globalException.getErrorCode());
    }

    @Test
    @DisplayName("동행 요청 거절 성공")
    void rejectAccompanyRequestSuccess() {
        // given
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));
        given(accompanyRepository.save(any())).willReturn(accompany);

        // when
        AccompanyStatusDto accompanyStatusDto = accompanyStatusService
            .rejectAccompanyRequest(member2.getEmail(), accompany.getId());

        // then
        assertThat(accompanyStatusDto.getRequestStatus()).isEqualTo(AccompanyStatus.REJECTED);
        assertThat(accompanyStatusDto.getRequestMemberId()).isEqualTo(
            accompany.getRequestMember().getId());
        assertThat(accompanyStatusDto.getRequestedMemberId()).isEqualTo(
            accompany.getRequestedMember().getId());
        assertThat(accompanyStatusDto.getPostId()).isEqualTo(accompany.getPost().getId());
    }

    @Test
    @DisplayName("동행 요청 취소 성공")
    void cancelAccompanyRequestSuccess() {
        // given
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.of(accompany));

        // when
        accompanyStatusService.cancelAccompanyRequest(accompany.getId());

        // then
        verify(accompanyRepository, times(1)).delete(accompany);
    }

    @Test
    @DisplayName("동행 요청 취소 실패, 요청 ID 없음")
    void cancelAccompanyRequestFail() {
        // given
        given(accompanyRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        GlobalException globalException = assertThrows(GlobalException.class,
            () -> accompanyStatusService.cancelAccompanyRequest(2L));

        // then
        Assertions.assertEquals(ErrorCode.ACCOMPANY_REQUEST_NOT_FOUND,
            globalException.getErrorCode());
    }
}
