package com.example.gotogetherbe.global.exception;

import com.example.gotogetherbe.global.exception.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorResponse {

  private ErrorCode errorCode;
  private HttpStatus httpStatus;
  private String errorMessage;
}
