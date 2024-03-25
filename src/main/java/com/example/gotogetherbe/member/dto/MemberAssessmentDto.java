package com.example.gotogetherbe.member.dto;

import com.example.gotogetherbe.member.entitiy.MemberAssessment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberAssessmentDto {

    private Long id;

    private Long memberId;

    private Double rating;

    private Long punctuality;

    private Long responsiveness;

    private Long photography;

    private Long manner;

    private Long navigation;

    private Long humor;

    private Long adaptability;

    public static MemberAssessmentDto from(MemberAssessment memberAssessment) {
        return MemberAssessmentDto.builder()
            .id(memberAssessment.getId())
            .memberId(memberAssessment.getMember().getId())
            .rating(memberAssessment.getRating())
            .punctuality(memberAssessment.getPunctualityCount())
            .responsiveness(memberAssessment.getResponsivenessCount())
            .photography(memberAssessment.getPhotographyCount())
            .manner(memberAssessment.getMannerCount())
            .navigation(memberAssessment.getNavigationCount())
            .humor(memberAssessment.getHumorCount())
            .adaptability(memberAssessment.getAdaptabilityCount())
            .build();
    }

}
