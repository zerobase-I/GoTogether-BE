package com.example.gotogetherbe.accompany.request.controller;

import com.example.gotogetherbe.accompany.request.dto.AccompanyStatusDto;
import com.example.gotogetherbe.accompany.request.service.AccompanyStatusService;
import com.example.gotogetherbe.auth.config.LoginUser;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accompany/request")
public class AccompanyRequestController {

    private final AccompanyStatusService accompanyStatusService;

    @PostMapping("/send/{postId}")
    public ResponseEntity<AccompanyStatusDto> sendAccompanyRequest(
        @LoginUser String username,
        @PathVariable Long postId
    ) {
        return ResponseEntity.ok(accompanyStatusService
            .sendAccompanyRequest(username, postId));
    }

    @PostMapping("/cancel/{requestId}")
    public void cancelAccompanyRequest(@PathVariable Long requestId) {
        accompanyStatusService.cancelAccompanyRequest(requestId);
    }

    @GetMapping("/send")
    public ResponseEntity<List<AccompanyStatusDto>> sentAccompanyRequest(@LoginUser String username) {
        return ResponseEntity.ok(accompanyStatusService.getSentAccompanyRequests(username));
    }

    @GetMapping("/receive")
    public ResponseEntity<List<AccompanyStatusDto>> receivedAccompanyRequest(@LoginUser String username) {
        return ResponseEntity.ok(accompanyStatusService.getReceivedAccompanyRequests(username));
    }

    @PostMapping("/approve/{requestId}")
    public ResponseEntity<AccompanyStatusDto> approveAccompanyRequest(
        @LoginUser String username,
        @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(accompanyStatusService.approveAccompanyRequest(username, requestId));
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<AccompanyStatusDto> rejectAccompanyRequest(
        @LoginUser String username,
        @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(accompanyStatusService.rejectAccompanyRequest(username, requestId));
    }

}
