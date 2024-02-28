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
   * 404 Not Found
   */

  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "이메일에 해당되는 사용자가 없습니다."),

  /**
   * 409 conflict
   */

  // User error
  DUPLICATE_USER(HttpStatus.CONFLICT, "이미 존재하는 회원입니다."),

  /**
   * 500 Internal Server Error
   */
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버에 오류가 발생했습니다."),
  API_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "외부 API 서버에 오류가 발생했습니다.")
  ;

  private final HttpStatus httpStatus;
  private final String description;
}
