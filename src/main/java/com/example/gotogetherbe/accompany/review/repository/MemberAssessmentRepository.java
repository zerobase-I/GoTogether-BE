package com.example.gotogetherbe.accompany.review.repository;

import com.example.gotogetherbe.accompany.review.entity.MemberAssessment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAssessmentRepository extends JpaRepository<MemberAssessment, Long> {

    Optional<MemberAssessment> findByMemberId(Long memberId);

}
