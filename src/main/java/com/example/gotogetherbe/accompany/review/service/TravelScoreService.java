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
        //targetMember의 travelScore가 null이면 기본값으로 설정된 travelScore를 생성, set
        if (member.getTravelScore() == null) {
            member.setTravelScore(
                TravelScore.builder()
                    .totalScore(0.0)
                    .totalReviewCount(0)
                    .rating(0.0)
                    .build()
            );
        }

        //점수 계산 후 업데이트 된 member객체 반환
        Member updatedMember = updateRating(member, score);

        //업데이트된 member객체로 travelScore 업데이트, cascade 설정으로 travelScore도 업데이트
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
