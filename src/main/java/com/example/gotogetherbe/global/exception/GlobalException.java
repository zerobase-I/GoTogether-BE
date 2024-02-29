package com.example.gotogetherbe.global.exception;

import com.example.gotogetherbe.global.exception.type.ErrorCode;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

  private final ErrorCode errorCode;

  public GlobalException(ErrorCode errorCode) {
    super(errorCode.getDescription());
    this.errorCode = errorCode;
  }
}
