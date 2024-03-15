package com.example.gotogetherbe.comment.dto;

import com.example.gotogetherbe.comment.entity.Comment;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
  private Long commentId;
  private Long memberId;
  private Long postId;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public static CommentDto from(Comment comment) {
    return CommentDto.builder()
        .commentId(comment.getId())
        .memberId(comment.getMember().getId())
        .postId(comment.getPost().getId())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .build();
  }
}
