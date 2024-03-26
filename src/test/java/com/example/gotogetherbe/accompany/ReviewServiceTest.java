package com.example.gotogetherbe.accompany;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.example.gotogetherbe.accompany.request.entity.Accompany;
import com.example.gotogetherbe.accompany.request.repository.AccompanyRepository;
import com.example.gotogetherbe.accompany.review.dto.MemberInfoDto;
import com.example.gotogetherbe.accompany.review.dto.ReviewDto;
import com.example.gotogetherbe.accompany.review.dto.ReviewWriteDto;
import com.example.gotogetherbe.accompany.review.entity.Review;
import com.example.gotogetherbe.accompany.review.repository.ReviewRepository;
import com.example.gotogetherbe.accompany.review.service.ReviewService;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.entitiy.type.MemberGender;
import com.example.gotogetherbe.member.entitiy.type.MemberLoginType;
import com.example.gotogetherbe.member.entitiy.type.MemberMbti;
import com.example.gotogetherbe.member.entitiy.type.MemberRoleType;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.member.service.MemberAssessmentService;
import com.example.gotogetherbe.notification.service.EventPublishService;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.entity.type.PostCategory;
import com.example.gotogetherbe.post.entity.type.PostGenderType;
import com.example.gotogetherbe.post.entity.type.PostRecruitmentStatus;
import com.example.gotogetherbe.post.entity.type.TravelCityType;
import com.example.gotogetherbe.post.entity.type.TravelCountryType;
import com.example.gotogetherbe.post.repository.PostRepository;
import java.time.LocalDateTime;
import java.util.List;
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
public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AccompanyRepository accompanyRepository;

    @Mock
    private EventPublishService eventPublishService;

    @Mock
    private MemberAssessmentService memberAssessmentService;

    private Post post;
    private Member member1;
    private Member member2;

    @BeforeEach
    void setUp() {
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
            .recruitmentStatus(PostRecruitmentStatus.COMPLETED)
            .category(PostCategory.ACTIVITY_TOUR)
            .title("Seoul Trip")
            .content("Let's go to Seoul!")
            .chatRoomExists(false)
            .currentPeople(2)
            .build();

        member1 = Member.builder() // 참여자, 리뷰어
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

        member2 = Member.builder() // 작성자, 리뷰대상자
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
    }

    @Test
    @DisplayName("동행 참여자 조회 성공")
    void getParticipantMembersSuccess() {
        // given
        Member member3 = Member.builder()
            .id(3L)
            .email("0000@gmail.com")
            .password("1234")
            .name("ABC")
            .nickname("ABC")
            .address("부산")
            .phoneNumber("010-1234-5678")
            .age(28)
            .gender(MemberGender.FEMALE)
            .profileImageUrl(null)
            .mbti(MemberMbti.ISFP)
            .instagramId("instagram123")
            .description("hi i'm kim")
            .loginType(MemberLoginType.EMAIL)
            .roleType(MemberRoleType.USER)
            .emailAuth(true)
            .posts(null)
            .build();

        List<Accompany> accompanies = List.of(
            Accompany.builder().requestMember(member1).requestedMember(member2).build(),
            Accompany.builder().requestMember(member3).requestedMember(member2).build()
        );

        List<Member> members = List.of(member1, member2, member3);

        given(memberRepository.findByEmail(anyString())).willReturn(java.util.Optional.of(member1));
        given(postRepository.findById(anyLong())).willReturn(java.util.Optional.of(post));
        given(reviewRepository.existsByPostAndReviewer(any(), any())).willReturn(false);
        given(accompanyRepository.findAllByPostIdAndStatus(anyLong(), any())).willReturn(
            accompanies);
        given(memberRepository.findAllByMemberIdIn(any())).willReturn(members);

        // when
        List<MemberInfoDto> participantMembers = reviewService
            .getParticipantMembers(member1.getEmail(), post.getId());

        // then
        assertThat(participantMembers.size()).isEqualTo(2);
        assertThat(participantMembers.get(0).getMemberId()).isEqualTo(member2.getId());
    }

    @Test
    @DisplayName("동행 참여자 조회 실패 - 완료된 동행이 아님")
    void getParticipantMembersFail() {
        // given
        post.setRecruitmentStatus(PostRecruitmentStatus.IN_PROGRESS);
        given(memberRepository.findByEmail(anyString())).willReturn(java.util.Optional.of(member1));
        given(postRepository.findById(anyLong())).willReturn(java.util.Optional.of(post));

        // when
        GlobalException globalException = assertThrows(GlobalException.class, () -> {
            reviewService.getParticipantMembers(member1.getEmail(), post.getId());
        });

        // then
        assertEquals(ErrorCode.UNCOMPLETED_ACCOMPANY, globalException.getErrorCode());
    }

    @Test
    @DisplayName("동행 참여자 조회 실패 - 중복 작성")
    void getParticipantMembersFail2() {
        // given
        given(memberRepository.findByEmail(anyString())).willReturn(java.util.Optional.of(member1));
        given(postRepository.findById(anyLong())).willReturn(java.util.Optional.of(post));
        given(reviewRepository.existsByPostAndReviewer(any(), any())).willReturn(true);

        // when
        GlobalException globalException = assertThrows(GlobalException.class, () -> {
            reviewService.getParticipantMembers(member1.getEmail(), post.getId());
        });

        // then
        assertEquals(ErrorCode.DUPLICATE_REVIEW, globalException.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 작성 성공")
    void writeReviewSuccess() {
        // given
        Review review = Review.builder()
            .id(1L)
            .post(post)
            .reviewer(member1)
            .targetMember(member2)
            .score(5.0)
            .punctuality(true)
            .responsiveness(true)
            .photography(false)
            .manner(false)
            .navigation(true)
            .humor(false)
            .adaptability(true)
            .createdAt(LocalDateTime.now())
            .build();
        List<Review> reviews = List.of(review);
        List<ReviewWriteDto> reviewWriteDtos = List.of(
            ReviewWriteDto.builder()
                .postId(1L)
                .targetMemberId(member2.getId())
                .score(5.0).punctuality(true)
                .responsiveness(true)
                .photography(false)
                .manner(false)
                .navigation(true)
                .humor(false)
                .adaptability(true)
                .build()
        );
        given(memberRepository.findByEmail(anyString())).willReturn(java.util.Optional.of(member1));
        given(memberRepository.findById(anyLong())).willReturn(java.util.Optional.of(member2));
        given(postRepository.findById(anyLong())).willReturn(java.util.Optional.of(post));
        given(reviewRepository.saveAll(any())).willReturn(reviews);

        // when
        List<ReviewDto> reviewDtos = reviewService.writeReview(member1.getEmail(), reviewWriteDtos);

        // then
        assertThat(reviewDtos.size()).isEqualTo(1);
        assertThat(reviewDtos.get(0).getTargetMemberId()).isEqualTo(member2.getId());
        assertThat(reviewDtos.get(0).getPostId()).isEqualTo(post.getId());
        assertThat(reviewDtos.get(0).getScore()).isEqualTo(5.0);
    }

    @Test
    @DisplayName("리뷰 작성 실패 - 리뷰 타겟을 찾지 못한 경우")
    void writeReviewFail() {
        // given
        List<ReviewWriteDto> reviewWriteDtos = List.of(
            ReviewWriteDto.builder()
                .postId(1L)
                .targetMemberId(member2.getId())
                .score(5.0).punctuality(true)
                .responsiveness(true)
                .photography(false)
                .manner(false)
                .navigation(true)
                .humor(false)
                .adaptability(true)
                .build()
        );
        given(memberRepository.findByEmail(anyString())).willReturn(java.util.Optional.of(member1));
        given(memberRepository.findById(anyLong())).willReturn(java.util.Optional.empty());
        given(postRepository.findById(anyLong())).willReturn(java.util.Optional.of(post));

        // when
        GlobalException globalException = assertThrows(GlobalException.class, () -> {
            reviewService.writeReview(member1.getEmail(), reviewWriteDtos);
        });

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, globalException.getErrorCode());
    }

}
