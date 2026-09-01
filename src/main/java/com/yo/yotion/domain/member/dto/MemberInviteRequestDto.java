package com.yo.yotion.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberInviteRequestDto {

    @NotBlank(message = "초대할 사용자의 이메일을 넣어주세요.")
    @Email(message = "올바른 이메일의 형식이 아닙니다.")
    private String email;


}
