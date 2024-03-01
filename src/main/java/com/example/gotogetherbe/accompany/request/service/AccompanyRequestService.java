package com.example.gotogetherbe.accompany.request.service;

import com.example.gotogetherbe.accompany.request.dto.AccompanyRequestDto;
import com.example.gotogetherbe.accompany.request.dto.AccompanyRequestSendDto;
import com.example.gotogetherbe.accompany.request.dto.RequestConfirmDto;
import java.util.List;

public interface AccompanyRequestService {
    boolean sendAccompanyRequest(AccompanyRequestSendDto accompanyRequestSendDto);

    List<AccompanyRequestDto> getSentAccompanyRequests(Long memberId);

    List<AccompanyRequestDto> getReceivedAccompanyRequests(Long memberId);

    boolean approveAccompanyRequest(RequestConfirmDto requestConfirmDto);

    boolean rejectAccompanyRequest(RequestConfirmDto requestConfirmDto);


}
