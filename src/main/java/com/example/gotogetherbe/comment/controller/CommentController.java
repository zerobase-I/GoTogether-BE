package com.example.gotogetherbe.comment.controller;

import com.example.gotogetherbe.auth.config.LoginUser;
import com.example.gotogetherbe.comment.dto.CommentDto;
import com.example.gotogetherbe.comment.dto.CommentRequest;
import com.example.gotogetherbe.comment.service.CommentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

  private final CommentService commentService;

  @PostMapping("/{postId}")
  public ResponseEntity<CommentDto> createComment(@LoginUser String email,
      @PathVariable Long postId,
      @RequestBody CommentRequest request) {
    return ResponseEntity.ok(commentService.createComment(email, postId, request));
  }

  @GetMapping("/{postId}")
  public ResponseEntity<List<CommentDto>> getCommentList(@PathVariable Long postId) {
    return ResponseEntity.ok(commentService.getCommentList(postId));
  }

  @PutMapping("/{commentId}")
  public ResponseEntity<CommentDto> updateComment(@LoginUser String email,
      @PathVariable Long commentId,
      @RequestBody CommentRequest request) {
    return ResponseEntity.ok(commentService.updateComment(email, commentId, request));
  }

  @DeleteMapping("/{commentId}")
  public ResponseEntity<CommentDto> deleteComment(@LoginUser String email,
      @PathVariable Long commentId) {
    return ResponseEntity.ok(commentService.deleteComment(email, commentId));
  }
}
