package com.example.gotogetherbe.accompany.review.service;

import static com.example.gotogetherbe.global.exception.type.ErrorCode.DUPLICATE_REVIEW;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_NOT_FOUND;

import com.example.gotogetherbe.accompany.review.dto.ReviewDto;
import com.example.gotogetherbe.accompany.review.dto.ReviewWriteDto;
import com.example.gotogetherbe.accompany.review.entity.Review;
import com.example.gotogetherbe.accompany.review.repository.ReviewRepository;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    @Override
    public boolean writeReview(ReviewWriteDto reviewDto) {
        existMember(reviewDto.getReviewerId());
        existMember(reviewDto.getTargetMemberId());
        //TODO postId 존재 확인
        //TODO 동행 완료 상태 && 리뷰어, 타겟 멤버가 동일한 동행에 참여한 멤버인지 확인
        checkDuplication(reviewDto);

        return !ObjectUtils.isEmpty(reviewRepository.save(reviewDto.toEntity()));
    }

    /**
     * 리뷰 중복 확인(리뷰어, 타겟 멤버, postId 세가지 항목 동시 일치 여부)
     */
    private void checkDuplication(ReviewWriteDto reviewDto) {
        if (reviewRepository.existByReviewerIdAndTargetMemberIdAndPostId(
            reviewDto.getReviewerId(),
            reviewDto.getTargetMemberId(),
            reviewDto.getPostId()
        )) {
            throw new GlobalException(DUPLICATE_REVIEW);
        }
    }

    private void existMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new GlobalException(USER_NOT_FOUND);
        }
    }

    @Override
    public List<ReviewDto> getReviews(Long memberId) {
        existMember(memberId);
        List<Review> reviews = reviewRepository.findAllByTargetMemberId(memberId);

        return reviews.stream().map(ReviewDto::from).toList();
    }
}
