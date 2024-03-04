package com.example.gotogetherbe.accompany.request.repository;

import com.example.gotogetherbe.accompany.request.entity.AccompanyRequest;
import com.example.gotogetherbe.member.entitiy.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccompanyRequestRepository extends JpaRepository<AccompanyRequest, Long> {

    List<AccompanyRequest> findAllByRequestMember(Member member);

    List<AccompanyRequest> findAllByRequestedMember(Member member);

    boolean existByRequestMemberAndRequestedMemberAndPost(
        Long requestMemberId, Long requestedMemberId, Long postId);

}
