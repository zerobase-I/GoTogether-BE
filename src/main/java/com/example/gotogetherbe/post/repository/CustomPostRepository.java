package com.example.gotogetherbe.post.repository;

import com.example.gotogetherbe.post.entity.Post;

import com.example.gotogetherbe.post.entity.PostDocument;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomPostRepository {
  Slice<Post> searchByTitle(Long postId, String title, Pageable pageable);

  Slice<PostDocument> searchByKeyword(List<String> keywords,
      LocalDateTime userStartDate, LocalDateTime userEndDate,
      Pageable pageable);

}
