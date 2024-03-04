package com.example.gotogetherbe.accompany.review.controller;

import com.example.gotogetherbe.accompany.review.dto.ReviewWriteDto;
import com.example.gotogetherbe.accompany.review.service.ReviewService;
import com.example.gotogetherbe.auth.config.LoginUser;
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
@RequestMapping("/api/accompany/review")
public class AccompanyReviewController {

    private final ReviewService reviewService;

    @PostMapping("")
    public ResponseEntity<?> writeReview(
        @LoginUser String username,
        @RequestBody ReviewWriteDto reviewWriteDto) {
        return ResponseEntity.ok().body(reviewService.writeReview(username, reviewWriteDto));
    }

    @GetMapping("")
    public ResponseEntity<?> getMyReviews(@LoginUser String username) {
        return ResponseEntity.ok().body(reviewService.getMyReviews(username));
    }

}
