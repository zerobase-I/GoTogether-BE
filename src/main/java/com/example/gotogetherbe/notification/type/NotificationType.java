package com.example.gotogetherbe.notification.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    ACCOMPANY_REQUEST("에 새로운 동행 요청이 왔습니다."),
    ACCOMPANY_REQUEST_APPROVAL("에 요청한 동행이 수락되었습니다."),
    ACCOMPANY_REQUEST_REJECT("에 요청한 동행이 거절되었습니다."),
    SCRAPED_POST("이 스크랩 되었습니다."),
    COMMENT("에 새로운 댓글이 달렸습니다."),
    REVIEW_WRITING_SUGGESTION("에 대한 동행 후기를 작성해주세요."),
    NEW_REVIEW("에 대한 후기가 작성되었습니다.");

    private final String message;
}
