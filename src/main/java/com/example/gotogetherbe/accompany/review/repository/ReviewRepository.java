package com.example.gotogetherbe.accompany.review.repository;

import com.example.gotogetherbe.accompany.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existByReviewerIdAndTargetMemberIdAndPostId(
        Long reviewerId, Long targetMemberId, Long postId);

    List<Review> findAllByTargetMemberId(Long targetMemberId);
}
