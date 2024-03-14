package com.example.gotogetherbe.accompany.review.service;

import com.example.gotogetherbe.accompany.review.entity.MemberAssessment;
import com.example.gotogetherbe.accompany.review.repository.MemberAssessmentRepository;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.member.entitiy.Member;
import java.text.DecimalFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAssessmentService {

    private final MemberAssessmentRepository travelScoreRepository;

    /**
     * 동행 평가 점수 업데이트
     *
     * @param member 평가 대상 멤버
     * @param score  평가 점수
     */
    @Transactional
    public void updateMemberAssessment(Member member, Double score) {
        MemberAssessment memberAssessment = travelScoreRepository.findByMemberId(member.getId())
            .orElseThrow(() -> new GlobalException(ErrorCode.TRAVEL_SCORE_NOT_FOUND));

        MemberAssessment updated = updateRating(memberAssessment, score);
        travelScoreRepository.save(updated);
    }

    /**
     * 평점 계산(역산)
     *
     * @param score  평가 점수
     * @param memberAssessment 평가 점수 객체
     * @return 평가 점수가 업데이트된 객체
     */
    private MemberAssessment updateRating(MemberAssessment memberAssessment, Double score) {
        double totalScore = (memberAssessment.getRating() * memberAssessment.getTotalReviewCount() + score);
        int newTotalReviewCount = memberAssessment.getTotalReviewCount() + 1;

        DecimalFormat form = new DecimalFormat("#.#");
        double newRating = Double.parseDouble(form.format(totalScore / newTotalReviewCount));

        memberAssessment.setTotalReviewCount(newTotalReviewCount);
        memberAssessment.setRating(newRating);

        return memberAssessment;
    }


}
