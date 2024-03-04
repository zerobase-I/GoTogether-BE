package com.example.gotogetherbe.accompany.review.service;

import static com.example.gotogetherbe.global.exception.type.ErrorCode.DUPLICATE_REVIEW;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_NOT_FOUND;

import com.example.gotogetherbe.accompany.review.dto.ReviewDto;
import com.example.gotogetherbe.accompany.review.dto.ReviewWriteDto;
import com.example.gotogetherbe.accompany.review.entity.Review;
import com.example.gotogetherbe.accompany.review.repository.ReviewRepository;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.post.entity.Post;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    /**
     * 리뷰 작성
     * @param email 로그인한 사용자 이메일
     * @param reviewDto 리뷰 작성 정보
     * @return 작성된 리뷰 정보
     */
    @Transactional
    public ReviewDto writeReview(String email, ReviewWriteDto reviewDto) {
        Member reviewer = getMemberByEmail(email);
        Member targetMember = memberRepository.findById(reviewDto.getTargetMemberId())
            .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));

        //TODO post 존재 확인
        //TODO 동행 완료 상태 && 리뷰어, 타겟 멤버가 동일한 동행에 참여한 멤버인지 확인
        //TODO post 수정
        checkDuplication(reviewer, targetMember, Post.builder().build());
        Review review = Review.builder()
            .reviewer(reviewer)
            .targetMember(targetMember)
            .post(Post.builder().build())
            .score(reviewDto.getScore())
            .content(reviewDto.getContent())
            .build();

        //TODO 스코어 업데이트

        return ReviewDto.from(reviewRepository.save(review));
    }

    /**
     * 로그인 한 사용자의 리뷰 조회
     * @param email 로그인한 사용자 이메일
     * @return 로그인한 사용자의 리뷰 리스트
     */
    public List<ReviewDto> getMyReviews(String email) {
        Member member = getMemberByEmail(email);
        List<Review> reviews = reviewRepository.findAllByTargetMember(member);

        return reviews.stream().map(ReviewDto::from).toList();
    }

    /**
     * 특정 사용자의 리뷰 조회
     * @param memberId 사용자 id
     * @return 특정 사용자의 리뷰 리스트
     */
    public List<ReviewDto> getReviews(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
        List<Review> reviews = reviewRepository.findAllByTargetMember(member);

        return reviews.stream().map(ReviewDto::from).toList();
    }

    /**
     * 리뷰 중복 확인(리뷰어, 타겟 멤버, postId 세가지 항목 동시 일치 여부)
     * @param reviewer 리뷰 작성자
     * @param targetMember 리뷰 대상자
     * @param post 게시글
     * 중복이 발생하면 GlobalException 발생
     */
    private void checkDuplication(Member reviewer, Member targetMember, Post post) {
        if (reviewRepository.existByReviewerAndTargetMemberAndPost(reviewer, targetMember, post)) {
            throw new GlobalException(DUPLICATE_REVIEW);
        }
    }

    /**
     * 이메일로 사용자 조회
     * @param email 사용자 이메일
     * @return member
     */
    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
    }
}
