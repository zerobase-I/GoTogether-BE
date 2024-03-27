package com.example.gotogetherbe.accompany.review.service;

import static com.example.gotogetherbe.accompany.request.type.AccompanyStatus.COMPLETED;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.DUPLICATE_REVIEW;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.POST_NOT_FOUND;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.UNCOMPLETED_ACCOMPANY;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_NOT_FOUND;
import static com.example.gotogetherbe.notification.type.NotificationType.NEW_REVIEW;

import com.example.gotogetherbe.accompany.request.entity.Accompany;
import com.example.gotogetherbe.accompany.request.repository.AccompanyRepository;
import com.example.gotogetherbe.accompany.review.dto.MemberInfoDto;
import com.example.gotogetherbe.accompany.review.dto.ReviewDto;
import com.example.gotogetherbe.accompany.review.dto.ReviewWriteDto;
import com.example.gotogetherbe.accompany.review.entity.Review;
import com.example.gotogetherbe.accompany.review.repository.ReviewRepository;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.member.service.MemberAssessmentService;
import com.example.gotogetherbe.notification.service.EventPublishService;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.entity.type.PostRecruitmentStatus;
import com.example.gotogetherbe.post.repository.PostRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MemberAssessmentService memberAssessmentService;
    private final AccompanyRepository accompanyRepository;
    private final EventPublishService eventPublishService;

    /**
     * 동행 참여자 조회
     *
     * @param email  로그인한 사용자 이메일(리뷰어)
     * @param postId 동행 게시글 id
     * @return 리뷰어를 제외한 동행 참여자 정보
     */
    public List<MemberInfoDto> getParticipantMembers(String email, Long postId) {
        Post post = getPostOrElseThrow(postId);
        Member reviewer = getMemberByEmailOrElseThrow(email);

        verifyCompletedStatus(post);
        verifyDuplicationReview(post, reviewer);

        List<Accompany> accompanies = accompanyRepository.findAllByPostIdAndStatus(postId,
            COMPLETED);
        List<Member> members = getParticipantsExcludingReviewer(reviewer, accompanies);

        return members.stream().map(MemberInfoDto::from).collect(Collectors.toList());
    }

    /**
     * 리뷰 작성
     *
     * @param email           로그인한 사용자 이메일
     * @param reviewWriteDtos 리뷰 작성 정보
     * @return 작성된 리뷰 정보
     */
    @Transactional
    public List<ReviewDto> writeReview(String email, List<ReviewWriteDto> reviewWriteDtos) {
        Member reviewer = getMemberByEmailOrElseThrow(email);
        Post post = getPostOrElseThrow(reviewWriteDtos.get(0).getPostId());

        List<Review> reviews = makeReview(reviewWriteDtos, post, reviewer);
        List<Review> savedReviews = reviewRepository.saveAll(reviews);

        memberAssessmentService.updateMemberAssessment(savedReviews);

        for (Review review : savedReviews) {
            eventPublishService.publishEvent(post, review.getTargetMember(), NEW_REVIEW);
        }

        return savedReviews.stream().map(ReviewDto::from).toList();
    }

    private Member getMemberByEmailOrElseThrow(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    }

    private Post getPostOrElseThrow(Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(() -> new GlobalException(POST_NOT_FOUND));
    }

    private void verifyDuplicationReview(Post post, Member reviewer) {
        if (reviewRepository.existsByPostAndReviewer(post, reviewer)) {
            throw new GlobalException(DUPLICATE_REVIEW);
        }
    }

    private static void verifyCompletedStatus(Post post) {
        if (post.getRecruitmentStatus() != PostRecruitmentStatus.COMPLETED) {
            throw new GlobalException(UNCOMPLETED_ACCOMPANY);
        }
    }

    /**
     * 리뷰어를 제외한 동행 참여자 조회
     *
     * @param reviewer    리뷰어
     * @param accompanies 동행 참여 정보
     * @return 리뷰어를 제외한 동행 참여자 리스트
     */
    private List<Member> getParticipantsExcludingReviewer(Member reviewer,
        List<Accompany> accompanies) {
        List<Member> memberList = new ArrayList<>();
        Set<Long> memberIds = new HashSet<>();

        accompanies.forEach(accompany -> {
            memberIds.add(accompany.getRequestMember().getId());
            memberIds.add(accompany.getRequestedMember().getId());
        });

        memberRepository.findAllByMemberIdIn(new ArrayList<>(memberIds))
            .forEach(member -> {
                if (!Objects.equals(reviewer, member)) {
                    memberList.add(member);
                }
            });

        return memberList;
    }

    /**
     * 리뷰 작성 정보로 리뷰 객체 생성
     *
     * @param post            참여한 동행 게시글
     * @param reviewWriteDtos 리뷰 작성 정보
     * @param reviewer        리뷰 작성자
     * @return 작성된 리뷰 정보
     */
    private List<Review> makeReview(List<ReviewWriteDto> reviewWriteDtos, Post post, Member reviewer
    ) {
        List<Review> reviews = new ArrayList<>();

        for (ReviewWriteDto reviewWriteDto : reviewWriteDtos) {
            Member targetMember = memberRepository.findById(reviewWriteDto.getTargetMemberId())
                .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));

            Review review = Review.builder()
                .reviewer(reviewer)
                .targetMember(targetMember)
                .post(post)
                .score(reviewWriteDto.getScore())
                .punctuality(reviewWriteDto.isPunctuality())
                .responsiveness(reviewWriteDto.isResponsiveness())
                .photography(reviewWriteDto.isPhotography())
                .manner(reviewWriteDto.isManner())
                .navigation(reviewWriteDto.isNavigation())
                .humor(reviewWriteDto.isHumor())
                .adaptability(reviewWriteDto.isAdaptability())
                .build();

            reviews.add(review);
        }
        return reviews;
    }

}
