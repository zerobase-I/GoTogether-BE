package com.example.gotogetherbe.accompany.review.dto;

import com.example.gotogetherbe.member.entitiy.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberInfoDto {

    private Long memberId;
    private String nickname;
    private String profileImageUrl;

    public static MemberInfoDto from(Member member) {
        return MemberInfoDto.builder()
            .memberId(member.getId())
            .nickname(member.getNickname())
            .profileImageUrl(member.getProfileImageUrl())
            .build();
    }

}
