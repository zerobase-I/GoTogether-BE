package com.example.gotogetherbe.post.controller;


import com.example.gotogetherbe.auth.config.LoginUser;
import com.example.gotogetherbe.post.dto.PostRequest;
import com.example.gotogetherbe.post.service.PostService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  /**
   * 게시물 생성
   */
  @PostMapping
  public ResponseEntity<?> createPost(@Valid @RequestPart(value = "request") PostRequest requestDto,
      @LoginUser String email,
      @RequestPart(value = "files", required = false) List<MultipartFile> files) {

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(postService.createPost(requestDto, email, files));
  }

  /**
   * 게시물 수정
   */
  @PutMapping("/{id}")
  public ResponseEntity<?> updatePost(@PathVariable Long id,
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
  public ResponseEntity<?> deletePost(@PathVariable Long id,
      @LoginUser String email) {
    postService.deletePost(id, email);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  /**
   * 게시물 상세 조회
   */
  @GetMapping("/{id}")
  public ResponseEntity<?> getPostDetail(@PathVariable Long id) {
    return ResponseEntity.ok(postService.readPost(id));
  }


}
