package com.example.gotogetherbe.accompany.review.service;

import com.example.gotogetherbe.accompany.review.entity.TravelScore;
import com.example.gotogetherbe.accompany.review.repository.TravelScoreRepository;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.member.entitiy.Member;
import java.text.DecimalFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TravelScoreService {

    private final TravelScoreRepository travelScoreRepository;

    /**
     * 동행 평가 점수 업데이트
     *
     * @param member 평가 대상 멤버
     * @param score  평가 점수
     */
    @Transactional
    public void updateTravelScore(Member member, Double score) {
        TravelScore travelScore = travelScoreRepository.findByMemberId(member.getId())
            .orElseThrow(() -> new GlobalException(ErrorCode.TRAVEL_SCORE_NOT_FOUND));

        TravelScore updated = updateRating(travelScore, score);
        travelScoreRepository.save(updated);
    }

    /**
     * 평점 계산(역산)
     *
     * @param score  평가 점수
     * @param travelScore 평가 점수 객체
     * @return 평가 점수가 업데이트된 객체
     */
    private TravelScore updateRating(TravelScore travelScore, Double score) {
        double totalScore = (travelScore.getRating() * travelScore.getTotalReviewCount() + score);
        int newTotalReviewCount = travelScore.getTotalReviewCount() + 1;

        DecimalFormat form = new DecimalFormat("#.#");
        double newRating = Double.parseDouble(form.format(totalScore / newTotalReviewCount));

        travelScore.setTotalReviewCount(newTotalReviewCount);
        travelScore.setRating(newRating);

        return travelScore;
    }


}
