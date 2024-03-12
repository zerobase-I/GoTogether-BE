package com.example.gotogetherbe.accompany.review.repository;

import com.example.gotogetherbe.accompany.review.entity.TravelScore;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelScoreRepository extends JpaRepository<TravelScore, Long> {

    Optional<TravelScore> findByMemberId(Long memberId);

}
