package com.example.gotogetherbe.post.controller;


import com.example.gotogetherbe.auth.config.LoginUser;
import com.example.gotogetherbe.post.dto.PostDocumentResponse;
import com.example.gotogetherbe.post.dto.PostRequest;
import com.example.gotogetherbe.post.dto.PostResponse;
import com.example.gotogetherbe.post.service.PostSearchService;
import com.example.gotogetherbe.post.service.PostService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;
  private final PostSearchService postSearchService;

  /**
   * 게시물 생성
   */
  @PostMapping
  public ResponseEntity<PostResponse> createPost(@Valid @RequestPart(value = "request") PostRequest requestDto,
      @LoginUser String email,
      @RequestPart(value = "files", required = false) List<MultipartFile> files) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(postService.createPost(requestDto, email, files));
  }

  /**
   * 게시물 수정
   */
  @PutMapping("/{id}")
  public ResponseEntity<PostResponse> updatePost(@PathVariable Long id,
      @Valid @RequestPart(value = "request") PostRequest requestDto,
      @LoginUser String email,
      @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
      @RequestPart(value = "imageIdsToDelete", required = false) List<Long> imageIdsToDelete) {

    return ResponseEntity.ok(postService
        .updatePost(id, requestDto, email, newImages, imageIdsToDelete));
  }

  /**
   * 게시물 삭제
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<PostResponse> deletePost(@PathVariable Long id,
      @LoginUser String email) {
    postService.deletePost(id, email);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  /**
   * 게시물 상세 조회
   */
  @GetMapping("/{id}")
  public ResponseEntity<PostResponse> getPostDetail(@PathVariable Long id) {
    return ResponseEntity.ok(postService.readPost(id));
  }


  /**
   * 내가 작성한 게시물 리스트 조회
   */
  @GetMapping("/list/{id}")
  public ResponseEntity<?> getPostList(@PathVariable Long id) {
    return null;
  }


  /**
   * 전체 게시물 조회
   */
  @GetMapping("/searchAll")
  public ResponseEntity<Page<PostDocumentResponse>> getPostList(@PageableDefault(sort = "createdAt"
      , direction = Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(postSearchService.findPosts(pageable));
  }

  /**
   * 키워드 포함 검색
   */
  @GetMapping("/keyword")
  public ResponseEntity<Slice<PostDocumentResponse>> searchPostAll(@RequestParam("keyword") List<String> keyword,
      @RequestParam(value = "startDate", required = false)  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
      @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
      @PageableDefault(sort = "createdAt", direction = Direction.DESC) Pageable pageable) {

    return ResponseEntity.ok(postSearchService.searchByKeyword(keyword, start, end, pageable));
  }

}
