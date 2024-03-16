package com.example.gotogetherbe.accompany.request.repository;

import com.example.gotogetherbe.accompany.request.entity.Accompany;
import com.example.gotogetherbe.accompany.request.type.AccompanyStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccompanyStatusRepository extends JpaRepository<Accompany, Long> {

    List<Accompany> findAllByRequestMemberIdAndStatusOrderByCreatedAtDesc(
        Long requestMemberId, AccompanyStatus status);

    List<Accompany> findAllByRequestedMemberIdAndStatusOrderByCreatedAtDesc(
        Long requestedMemberId, AccompanyStatus status);

    boolean existsByRequestMember_IdAndPost_Id(
        Long requestMemberId, Long postId);

}
