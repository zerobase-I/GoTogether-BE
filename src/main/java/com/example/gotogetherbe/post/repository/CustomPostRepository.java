package com.example.gotogetherbe.post.repository;

import com.example.gotogetherbe.post.entity.Post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CustomPostRepository {
  Slice<Post> searchByTitle(Long postId, String title, Pageable pageable);


}
