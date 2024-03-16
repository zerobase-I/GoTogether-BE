package com.example.gotogetherbe.accompany.review.controller;

import com.example.gotogetherbe.accompany.review.dto.MemberAssessmentDto;
import com.example.gotogetherbe.accompany.review.dto.MemberInfoDto;
import com.example.gotogetherbe.accompany.review.dto.ReviewDto;
import com.example.gotogetherbe.accompany.review.dto.ReviewWriteDto;
import com.example.gotogetherbe.accompany.review.service.ReviewService;
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
@RequestMapping("/api/accompany/review")
public class AccompanyReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{postId}")
    public ResponseEntity<List<MemberInfoDto>> getParticipantMembers(
        @LoginUser String username,
        @PathVariable Long postId
    ) {
        return ResponseEntity.ok(reviewService.getParticipantMembers(username, postId));
    }

    @PostMapping("/submit")
    public ResponseEntity<List<ReviewDto>> submitReview(
        @LoginUser String username,
        @RequestBody List<ReviewWriteDto> reviewWriteDtos) {
        return ResponseEntity.ok(reviewService.writeReview(username, reviewWriteDtos));
    }

    @GetMapping("/assessment/{memberId}")
    public ResponseEntity<MemberAssessmentDto> getReviewDetail(@PathVariable Long memberId) {
        return ResponseEntity.ok().body(reviewService.getMemberAssessment(memberId));
    }

}
