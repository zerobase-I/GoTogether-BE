package com.example.gotogetherbe.accompany.review.repository;

import com.example.gotogetherbe.accompany.review.entity.Review;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.post.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existByReviewerAndTargetMemberAndPost(Member reviewer, Member targetMember, Post post);

    List<Review> findAllByTargetMemberId(Long targetMemberId);
}
