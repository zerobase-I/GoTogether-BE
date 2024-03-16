package com.example.gotogetherbe.comment.service;

import static com.example.gotogetherbe.global.exception.type.ErrorCode.COMMENT_NOT_FOUND;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.POST_NOT_FOUND;
import static com.example.gotogetherbe.global.exception.type.ErrorCode.USER_NOT_FOUND;

import com.example.gotogetherbe.comment.dto.CommentDto;
import com.example.gotogetherbe.comment.dto.CommentRequest;
import com.example.gotogetherbe.comment.entity.Comment;
import com.example.gotogetherbe.comment.repository.CommentRepository;
import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.repository.MemberRepository;
import com.example.gotogetherbe.post.entity.Post;
import com.example.gotogetherbe.post.repository.PostRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
  private final MemberRepository memberRepository;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;

  /**
   * 댓글 생성
   *
   * @param email     댓글을 생성하는 회원의 이메일
   * @param postId     댓글을 생성할 게시글ID
   * @param request    생성할 댓글 내용
   * @return 생성된 댓글의 정보를 포함한 CommentDto 객체
   */
  public CommentDto createComment(String email, Long postId, CommentRequest request) {
    Member member = getMemberOrThrow(email);
    Post post = getPostOrThrow(postId);

    Comment comment = commentRepository.save(Comment.builder()
            .member(member)
            .post(post)
            .content(request.getContent())
            .build());

    return CommentDto.from(comment);
  }

  /**
   * 댓글 목록 조회
   *
   * @param postId     댓글을 조회할 게시글ID
   * @return 조회된 댓글의 정보를 포함한 CommentDto 목록
   */
  public List<CommentDto> getCommentList(Long postId) {
    return commentRepository.findAllByPostId(postId).stream().map(CommentDto::from).collect(Collectors.toList());
  }

  /**
   * 댓글 수정
   *
   * @param email     댓글을 수정하는 회원의 이메일
   * @param commentId     수정할 댓글 아이디
   * @param request    수정할 댓글 내용
   * @return 수정된 댓글의 정보를 포함한 CommentDto 객체
   */
  public CommentDto updateComment(String email, Long commentId, CommentRequest request) {
    Member member = getMemberOrThrow(email);
    Comment comment = getCommentOrThrow(commentId);

    if (!Objects.equals(member.getId(), comment.getMember().getId())) {
      throw new GlobalException(ErrorCode.NOT_MEMBER_COMMENT);
    }

    comment.updateContent(request.getContent());

    return CommentDto.from(commentRepository.save(comment));
  }


  /**
   * 댓글 삭제
   *
   * @param email     댓글을 삭제하는 회원의 이메일
   * @param commentId     삭제할 댓글 아이디
   * @return 삭제된 댓글의 정보를 포함한 CommentDto 객체
   */
  public CommentDto deleteComment(String email, Long commentId) {
    Member member = getMemberOrThrow(email);
    Comment comment = getCommentOrThrow(commentId);

    if (!Objects.equals(member.getId(), comment.getMember().getId())) {
      throw new GlobalException(ErrorCode.NOT_MEMBER_COMMENT);
    }

    commentRepository.delete(comment);

    return CommentDto.from(comment);
  }

  private Member getMemberOrThrow(String email) {
    return memberRepository.findByEmail(email)
        .orElseThrow(() -> new GlobalException(USER_NOT_FOUND));
  }

  private Post getPostOrThrow(Long postId) {
    return postRepository.findById(postId)
        .orElseThrow(() -> new GlobalException(POST_NOT_FOUND));
  }

  private Comment getCommentOrThrow(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new GlobalException(COMMENT_NOT_FOUND));
  }
}
