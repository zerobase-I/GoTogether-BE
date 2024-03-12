package com.example.gotogetherbe.accompany.request.repository;

import com.example.gotogetherbe.accompany.request.entity.AccompanyRequest;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.post.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccompanyRequestRepository extends JpaRepository<AccompanyRequest, Long> {

    List<AccompanyRequest> findAllByRequestMember_Email(String email);

    List<AccompanyRequest> findAllByRequestedMember_Email(String email);

    boolean existsByRequestMemberAndRequestedMemberAndPost(
        Member requestMember, Member requestedMember, Post post);

}
