package com.example.gotogetherbe.accompany.review.service;

import static com.example.gotogetherbe.global.exception.type.ErrorCode.MEMBER_ASSESSMENT_NOT_FOUND;

import com.example.gotogetherbe.accompany.review.entity.MemberAssessment;
import com.example.gotogetherbe.accompany.review.entity.Review;
import com.example.gotogetherbe.accompany.review.repository.MemberAssessmentRepository;
import com.example.gotogetherbe.global.exception.GlobalException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAssessmentService {

    private final MemberAssessmentRepository memberAssessmentRepository;

    /**
     * 회원 평가 업데이트
     *
     * @param reviews 리뷰 목록
     */
    @Transactional
    public void updateMemberAssessment(List<Review> reviews) {
        List<MemberAssessment> memberAssessments = new ArrayList<>();

        for (Review review : reviews) {
            MemberAssessment memberAssessment = memberAssessmentRepository.findByMemberId(
                    review.getTargetMember().getId())
                .orElseThrow(() -> new GlobalException(MEMBER_ASSESSMENT_NOT_FOUND));

            updateRating(memberAssessment, review.getScore());
            updateMemberAssessmentCount(memberAssessment, review);
            memberAssessments.add(memberAssessment);
        }

        memberAssessmentRepository.saveAll(memberAssessments);
    }

    /**
     * 평점 계산(역산)
     *
     * @param score            평가 점수
     * @param memberAssessment 평가 점수 객체
     */
    private void updateRating(MemberAssessment memberAssessment, Double score) {
        double totalScore = (memberAssessment.getRating() * memberAssessment.getTotalReviewCount()
            + score);
        int newTotalReviewCount = memberAssessment.getTotalReviewCount() + 1;

        DecimalFormat form = new DecimalFormat("#.#");
        double newRating = Double.parseDouble(form.format(totalScore / newTotalReviewCount));

        memberAssessment.updateRatingAndTotalReviewCount(newRating, newTotalReviewCount);
    }

    /**
     * 회원 평가 수 업데이트
     *
     * @param memberAssessment 회원 평가 객체
     * @param review           리뷰 객체
     */
    private void updateMemberAssessmentCount(MemberAssessment memberAssessment, Review review) {
        memberAssessment.updateAssessmentCount(review.isPunctuality(), "punctuality");
        memberAssessment.updateAssessmentCount(review.isResponsiveness(), "responsiveness");
        memberAssessment.updateAssessmentCount(review.isPhotography(), "photography");
        memberAssessment.updateAssessmentCount(review.isManner(), "manner");
        memberAssessment.updateAssessmentCount(review.isNavigation(), "navigation");
        memberAssessment.updateAssessmentCount(review.isHumor(), "humor");
        memberAssessment.updateAssessmentCount(review.isAdaptability(), "adaptability");
    }

}
