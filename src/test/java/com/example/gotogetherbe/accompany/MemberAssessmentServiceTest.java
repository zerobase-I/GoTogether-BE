package com.example.gotogetherbe.accompany;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.example.gotogetherbe.accompany.review.entity.MemberAssessment;
import com.example.gotogetherbe.accompany.review.entity.Review;
import com.example.gotogetherbe.accompany.review.repository.MemberAssessmentRepository;
import com.example.gotogetherbe.accompany.review.service.MemberAssessmentService;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.entitiy.type.MemberGender;
import com.example.gotogetherbe.member.entitiy.type.MemberLoginType;
import com.example.gotogetherbe.member.entitiy.type.MemberMbti;
import com.example.gotogetherbe.member.entitiy.type.MemberRoleType;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.entity.type.PostCategory;
import com.example.gotogetherbe.post.entity.type.PostGenderType;
import com.example.gotogetherbe.post.entity.type.PostRecruitmentStatus;
import com.example.gotogetherbe.post.entity.type.TravelCityType;
import com.example.gotogetherbe.post.entity.type.TravelCountryType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
public class MemberAssessmentServiceTest {

    @InjectMocks
    private MemberAssessmentService memberAssessmentService;

    @Mock
    private MemberAssessmentRepository memberAssessmentRepository;

    private Member member1;
    private Member member2;
    private Post post;

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
    @DisplayName("회원 평가 업데이트 성공")
    public void updateMemberAssessmentSuccess() {
        // given
        Review review = Review.builder()
            .id(1L)
            .targetMember(member1)
            .reviewer(member2)
            .post(post)
            .score(5.0)
            .punctuality(true)
            .responsiveness(true)
            .adaptability(false)
            .humor(false)
            .navigation(true)
            .photography(true)
            .manner(true)
            .createdAt(LocalDateTime.now())
            .build();
        List<Review> reviews = List.of(review);

        MemberAssessment memberAssessment = MemberAssessment.builder()
            .id(1L)
            .member(member1)
            .totalReviewCount(1L)
            .rating(2.0)
            .punctualityCount(1L)
            .responsivenessCount(1L)
            .photographyCount(1L)
            .mannerCount(1L)
            .navigationCount(1L)
            .humorCount(0L)
            .adaptabilityCount(0L)
            .build();

        given(memberAssessmentRepository.findByMemberId(member1.getId()))
            .willReturn(Optional.of(memberAssessment));

        // when
        memberAssessmentService.updateMemberAssessment(reviews);

        // then
        assertThat(memberAssessment.getMember()).isEqualTo(member1);
        assertThat(memberAssessment.getTotalReviewCount()).isEqualTo(2L);
        assertThat(memberAssessment.getRating()).isEqualTo(3.5);
        assertThat(memberAssessment.getPunctualityCount()).isEqualTo(2L);
        assertThat(memberAssessment.getHumorCount()).isEqualTo(0L);
    }

}
