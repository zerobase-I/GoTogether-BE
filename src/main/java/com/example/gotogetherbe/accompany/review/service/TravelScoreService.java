package com.example.gotogetherbe.accompany.review.service;

import com.example.gotogetherbe.accompany.review.entity.TravelScore;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TravelScoreService {

    private final MemberRepository memberRepository;

    /**
     * 동행 평가 점수 업데이트
     *
     * @param member 평가 대상 멤버
     * @param score  평가 점수
     */
    @Transactional
    public void updateTravelScore(Member member, Double score) {
        Member updatedMember = updateRating(member, score);

        memberRepository.save(updatedMember);
    }

    /**
     * 평점 계산
     *
     * @param score  평가 점수
     * @param member 평가 대상 멤버
     * @return 평가 점수가 업데이트된 멤버 객체
     */
    private Member updateRating(Member member, Double score) {
        TravelScore travelScore = member.getTravelScore();

        Double newTotalScore = travelScore.getTotalScore() + score;
        Integer newTotalReviewCount = travelScore.getTotalReviewCount() + 1;
        Double newRating = (double) Math.round((newTotalScore / newTotalReviewCount) * 10) / 10;

        travelScore.setTotalScore(newTotalScore);
        travelScore.setTotalReviewCount(newTotalReviewCount);
        travelScore.setRating(newRating);

        return member;
    }


}
