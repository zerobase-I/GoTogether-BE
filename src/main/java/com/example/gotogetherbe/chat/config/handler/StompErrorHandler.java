package com.example.gotogetherbe.chat.config.handler;


import com.example.gotogetherbe.global.exception.GlobalException;
import com.example.gotogetherbe.global.exception.type.ErrorCode;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Slf4j
@Component
public class StompErrorHandler extends StompSubProtocolErrorHandler {

  private static final byte[] EMPTY_PAYLOAD = new byte[0];

  public StompErrorHandler() {
    super();
  }

  @Override
  public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage,
      Throwable ex) {

    if (ex instanceof MessageDeliveryException) {
      ex = ex.getCause();
    }

    if (ex instanceof GlobalException) {
      return handleGlobalException(((GlobalException) ex).getErrorCode());
    }

    return handleException(ex);
  }

  private Message<byte[]> handleException(Throwable ex) {
    log.error("stomp protocol exception is occurred. ", ex);
    return errorMessage(ex.getMessage(), HttpStatus.BAD_REQUEST.name());
  }

  private Message<byte[]> handleGlobalException(ErrorCode errorCode) {
    return errorMessage(errorCode.getDescription(), errorCode.name());
  }

  private Message<byte[]> errorMessage(String errorMessage, String errorCodeName) {
    StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
    accessor.setMessage(errorCodeName);
    accessor.setLeaveMutable(true);

    String response = String.format("[%s] %s", errorCodeName, errorMessage);
    return MessageBuilder.createMessage(errorMessage != null ?
            response.getBytes(StandardCharsets.UTF_8) : EMPTY_PAYLOAD,
        accessor.getMessageHeaders());
  }
}
