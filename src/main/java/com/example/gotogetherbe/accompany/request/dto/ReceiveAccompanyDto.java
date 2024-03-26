package com.example.gotogetherbe.accompany.request.dto;

import com.example.gotogetherbe.accompany.request.entity.Accompany;
import com.example.gotogetherbe.accompany.request.type.AccompanyStatus;
import com.example.gotogetherbe.member.entitiy.Member;
import com.example.gotogetherbe.member.entitiy.type.MemberMbti;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiveAccompanyDto {

    private Long id;

    private Long requestMemberId;

    private String nickname;

    private MemberMbti mbti;

    private String profileImage;

    private Long requestedMemberId;

    private Long postId;

    private String postTitle;

    private AccompanyStatus requestStatus;

    private String createdAt;

    public static ReceiveAccompanyDto from(Accompany accompanyRequest, String postTitle, Member member) {
        return ReceiveAccompanyDto.builder()
            .id(accompanyRequest.getId())
            .requestMemberId(accompanyRequest.getRequestMember().getId())
            .nickname(member.getNickname())
            .profileImage(member.getProfileImageUrl())
            .mbti(member.getMbti())
            .requestedMemberId(accompanyRequest.getRequestedMember().getId())
            .postId(accompanyRequest.getPost().getId())
            .postTitle(postTitle)
            .requestStatus(accompanyRequest.getStatus())
            .createdAt(accompanyRequest.getCreatedAt().toString())
            .build();
    }
}
