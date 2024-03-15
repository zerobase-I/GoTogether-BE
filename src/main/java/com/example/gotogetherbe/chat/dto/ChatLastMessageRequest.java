package com.example.gotogetherbe.chat.dto;

import org.springframework.util.ObjectUtils;

public record ChatLastMessageRequest(Long lastMessageId, Integer limit) {

  public ChatLastMessageRequest {
    if (ObjectUtils.isEmpty(limit) || limit == 0) {
      limit = 0;
    }
  }
}
