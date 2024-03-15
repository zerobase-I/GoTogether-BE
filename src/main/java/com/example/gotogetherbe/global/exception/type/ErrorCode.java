package com.example.gotogetherbe.global.exception.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  /**
   * 400 Bad Request
   */
  // Common error
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

  // User error
  PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다"),
  INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "인증번호가 일치하지 않습니다."),
  EMAIL_NOT_VERITY(HttpStatus.BAD_REQUEST, "이메일 인증이 되지 않았습니다."),
  ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 등록되어있습니다."),
  BOOKMARK_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 북마크가 없습니다."),
  WRITE_NOT_YOURSELF(HttpStatus.BAD_REQUEST, "본인이 작성한 글만 수정, 삭제가 가능합니다."),
  CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 카테고리가 없습니다."),
  POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 글이 없습니다."),
  POST_CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 글 카테고리가 없습니다."),
  COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당하는 댓글이 없습니다."),
  PROFILE_IMAGE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "프로필 이미지 업로드 중 오류가 발생했습니다."),
  POST_IMAGE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "게시물 이미지 업로드 중 오류가 발생했습니다."),
  ALREADY_DELETED_CHATROOM(HttpStatus.BAD_REQUEST, "삭제된 채팅방입니다."),
  ALREADY_CREATED_CHATROOM(HttpStatus.BAD_REQUEST, "이미 생성된 채팅방입니다."),
  CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "채팅방을 찾을 수 없습니다."),
  CHAT_MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "채팅 참여회원을 찾을 수 없습니다."),
  CHATROOM_IS_EMPTY(HttpStatus.BAD_REQUEST, "참여중인 회원이 없습니다."),
  NOT_BELONG_TO_CHAT_MEMBER(HttpStatus.BAD_REQUEST, "채팅방의 참여중인 회원이 아닙니다."),
  UNCOMPLETED_ACCOMPANY(HttpStatus.BAD_REQUEST, "완료되지 않은 동행입니다."),
  NOT_SAME_ACCOMPANY_MEMBER(HttpStatus.BAD_REQUEST, "같은 동행에 참여한 회원이 아닙니다."),


  /**
   * 401 Unauthorized
   */
  // User error
  USER_AUTHORITY_NOT_MATCH(HttpStatus.UNAUTHORIZED, "사용자 권한이 없습니다."),

  // Security(jwt) error
  UNKNOWN_ERROR(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않는 토큰입니다."),
  UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다."),
  WRONG_TYPE_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 형식의 토큰입니다."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다. 재발급이 필요합니다."),

  /**
   * 403 Forbidden
   */
  USER_MISMATCH(HttpStatus.FORBIDDEN, "다른 사용자의 요청을 승인할 수 없습니다."),
  POST_AUTHOR_MISMATCH(HttpStatus.FORBIDDEN, "동행 게시글 작성자와 요청을 받는 사용자가 일치하지 않습니다."),
  
  /**
   * 404 Not Found
   */
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자가 없습니다."),
  ACCOMPANY_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "동행 요청을 찾을 수 없습니다."),
  
  /**
   * 406 Not Acceptable
   */
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),

  /**
   * 409 conflict
   */

  // User error
  DUPLICATE_USER(HttpStatus.CONFLICT, "이미 존재하는 회원입니다."),
  DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다."),
  MEMBER_POST_INCORRECT(HttpStatus.CONFLICT, "회원의 게시글이 아닙니다."),
  ALREADY_PARTICIPANT_MEMBER(HttpStatus.CONFLICT, "이미 참여중인 회원입니다."),
  DUPLICATE_ACCOMPANY_REQUEST(HttpStatus.CONFLICT, "동일한 요청이 존재합니다."),

  DUPLICATE_REVIEW(HttpStatus.CONFLICT, "이미 리뷰를 작성하셨습니다."),

  /**
   * 500 Internal Server Error
   */
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버에 오류가 발생했습니다."),
  API_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "외부 API 서버에 오류가 발생했습니다.")
  ;

  private final HttpStatus httpStatus;
  private final String description;
}
