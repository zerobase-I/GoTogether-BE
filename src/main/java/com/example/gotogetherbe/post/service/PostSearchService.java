package com.example.gotogetherbe.post.service;


import com.example.gotogetherbe.post.dto.PostDocumentResponse;
import com.example.gotogetherbe.post.repository.PostSearchRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostSearchService {

  private final PostSearchRepository postSearchRepository;

  @Transactional(readOnly = true)
  public Page<PostDocumentResponse> findPosts(Pageable pageable) {
    return postSearchRepository.findAll(pageable).map(PostDocumentResponse::fromDocument);
  }

  @Transactional(readOnly = true)
  public Slice<PostDocumentResponse> searchByKeyword(List<String> keyword,
      LocalDateTime userStartDate, LocalDateTime userEndDate,
      Pageable pageable
  ) {
    return postSearchRepository.searchByKeyword(keyword,
        userStartDate, userEndDate,pageable).map(PostDocumentResponse::fromDocument);
  }

}
