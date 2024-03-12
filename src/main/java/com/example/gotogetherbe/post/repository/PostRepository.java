package com.example.gotogetherbe.post.repository;

import com.example.gotogetherbe.post.entity.Post;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {
    Optional<Post> findByMemberIdAndId(Long memberId, Long postId);
}
