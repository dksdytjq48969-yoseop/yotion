package com.yo.yotion.domain.member.dto;

import com.yo.yotion.domain.member.entity.Member;
import com.yo.yotion.domain.member.entity.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponseDto {

    private Long memberId;
    private Long pageId;
    private Long userId;
    private String nickname;
    private MemberRole role;

    public static MemberResponseDto from (Member member) {
        return MemberResponseDto.builder()
                .memberId(member.getId())
                .pageId(member.getPage().getId())
                .userId(member.getUser().getId())
                .nickname(member.getUser().getNickname())
                .role(member.getRole())
                .build();
    }

}
