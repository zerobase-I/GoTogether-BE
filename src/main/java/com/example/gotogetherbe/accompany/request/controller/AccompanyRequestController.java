package com.example.gotogetherbe.accompany.request.controller;

import com.example.gotogetherbe.accompany.request.dto.AccompanyRequestDto;
import com.example.gotogetherbe.accompany.request.dto.AccompanyRequestSendDto;
import com.example.gotogetherbe.accompany.request.service.AccompanyRequestService;
import com.example.gotogetherbe.auth.config.LoginUser;
import java.util.List;
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
    public ResponseEntity<AccompanyRequestDto> sendAccompanyRequest(
        @LoginUser String username,
        @RequestBody AccompanyRequestSendDto accompanyRequestSendDto
    ) {
        return ResponseEntity.ok(accompanyRequestService
            .sendAccompanyRequest(username, accompanyRequestSendDto));
    }

    @PostMapping("/cancel/{requestId}")
    public ResponseEntity<String> cancelAccompanyRequest( @PathVariable Long requestId) {
        accompanyRequestService.cancelAccompanyRequest(requestId);
        return ResponseEntity.ok().body("Accompany request canceled successfully.");
    }

    @GetMapping("/send")
    public ResponseEntity<List<AccompanyRequestDto>> sentAccompanyRequest(@LoginUser String username) {
        return ResponseEntity.ok(accompanyRequestService.getSentAccompanyRequests(username));
    }

    @GetMapping("/receive")
    public ResponseEntity<List<AccompanyRequestDto>> receivedAccompanyRequest(@LoginUser String username) {
        return ResponseEntity.ok(accompanyRequestService.getReceivedAccompanyRequests(username));
    }

    @PostMapping("/approve/{requestId}")
    public ResponseEntity<AccompanyRequestDto> approveAccompanyRequest(
        @LoginUser String username, @PathVariable Long requestId
    ) {
        return ResponseEntity.ok()
            .body(accompanyRequestService.approveAccompanyRequest(username, requestId));
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<AccompanyRequestDto> rejectAccompanyRequest(
        @LoginUser String username, @PathVariable Long requestId
    ) {
        return ResponseEntity.ok()
            .body(accompanyRequestService.rejectAccompanyRequest(username, requestId));
    }

}
