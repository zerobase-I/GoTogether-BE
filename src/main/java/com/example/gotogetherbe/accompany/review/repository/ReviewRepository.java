package com.example.gotogetherbe.accompany.review.repository;

import com.example.gotogetherbe.accompany.review.entity.Review;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.post.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByReviewerAndTargetMemberAndPost(Member reviewer, Member targetMember, Post post);

    List<Review> findAllByTargetMember_Id(Long memberId);
}
