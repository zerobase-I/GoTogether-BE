package com.example.gotogetherbe.post.repository;

import com.example.gotogetherbe.post.entity.Post;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {

  List<Post> findAllByStartDate(LocalDateTime startDate);
  List<Post> findAllByEndDate(LocalDateTime endDate);
}
