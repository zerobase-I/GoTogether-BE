package com.example.gotogetherbe.accompany.review.repository;

import com.example.gotogetherbe.accompany.review.entity.Review;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByPostAndReviewer(Post post, Member member);
}
