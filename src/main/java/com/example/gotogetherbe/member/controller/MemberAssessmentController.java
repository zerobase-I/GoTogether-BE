package com.example.gotogetherbe.member.controller;

import com.example.gotogetherbe.member.dto.MemberAssessmentDto;
import com.example.gotogetherbe.member.service.MemberAssessmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member/assessment")
@RequiredArgsConstructor
public class MemberAssessmentController {

    private final MemberAssessmentService memberAssessmentService;

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberAssessmentDto> getReviewDetail(@PathVariable Long memberId) {
        return ResponseEntity.ok().body(memberAssessmentService.getMemberAssessment(memberId));
    }
}
