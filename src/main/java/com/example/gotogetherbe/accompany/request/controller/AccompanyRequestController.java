package com.example.gotogetherbe.accompany.request.controller;

import com.example.gotogetherbe.accompany.request.dto.AccompanyRequestSendDto;
import com.example.gotogetherbe.accompany.request.dto.RequestConfirmDto;
import com.example.gotogetherbe.accompany.request.service.AccompanyRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accompany/request")
public class AccompanyRequestController {
    private final AccompanyRequestService accompanyRequestService;
    @PostMapping("/send")
    public ResponseEntity<?> sendAccompanyRequest(
        @RequestBody AccompanyRequestSendDto accompanyRequestSendDto) {
        return ResponseEntity.ok()
            .body(accompanyRequestService.sendAccompanyRequest(accompanyRequestSendDto));
    }

    @GetMapping("/send/{memberId}")
    public ResponseEntity<?> sentAccompanyRequest(@PathVariable Long memberId) {
        return ResponseEntity.ok().body(accompanyRequestService.getSentAccompanyRequests(memberId));
    }

    @GetMapping("/receive/{memberId}")
    public ResponseEntity<?> receivedAccompanyRequest(@PathVariable Long memberId) {
        return ResponseEntity.ok().body(accompanyRequestService.getReceivedAccompanyRequests(memberId));
    }

    @PostMapping("/approve")
    public ResponseEntity<?> approveAccompanyRequest(
        @RequestBody RequestConfirmDto requestConfirmDto) {
        return ResponseEntity.ok()
            .body(accompanyRequestService.approveAccompanyRequest(requestConfirmDto));
    }

    @PostMapping("/reject")
    public ResponseEntity<?> rejectAccompanyRequest(
        @RequestBody RequestConfirmDto requestConfirmDto) {
        return ResponseEntity.ok()
            .body(accompanyRequestService.rejectAccompanyRequest(requestConfirmDto));
    }

}
