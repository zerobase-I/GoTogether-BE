package com.example.gotogetherbe.member.repository;

import com.example.gotogetherbe.member.entitiy.MemberAssessment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAssessmentRepository extends JpaRepository<MemberAssessment, Long> {

    Optional<MemberAssessment> findByMemberId(Long memberId);

}
