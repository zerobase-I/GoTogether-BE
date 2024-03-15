package com.example.gotogetherbe.accompany.request.repository;

import com.example.gotogetherbe.accompany.request.entity.AccompanyRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccompanyRequestRepository extends JpaRepository<AccompanyRequest, Long> {

    List<AccompanyRequest> findAllByRequestMemberIdOrderByCreatedAtDesc(Long requestMemberId);

    List<AccompanyRequest> findAllByRequestedMemberIdOrderByCreatedAtDesc(Long requestedMemberId);

    boolean existsByRequestedMember_IdAndRequestedMember_IdAndPost_Id(
        Long requestMemberId, Long requestedMemberId, Long postId);

}
