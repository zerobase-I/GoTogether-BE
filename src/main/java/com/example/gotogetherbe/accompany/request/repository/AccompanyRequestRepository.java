package com.example.gotogetherbe.accompany.request.repository;

import com.example.gotogetherbe.accompany.request.entity.AccompanyRequest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccompanyRequestRepository extends JpaRepository<AccompanyRequest, Long> {
    List<AccompanyRequest> findAllByRequestMemberId(Long memberId);
    List<AccompanyRequest> findAllByRequestedMemberId(Long memberId);
    boolean existByRequestMemberIdAndRequestedMemberIdAndPostId(
        Long requestMemberId, Long requestedMemberId, Long postId);

}
