package com.yo.yotion.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequestDto {

    @NotBlank(message = "닉네임은 필수로 입력되어야 합니다.")
    @Size(min = 1, max = 20, message = "닉네임은 1-20자 사이여갸 합니다.")
    private String nickname;

    @NotBlank(message = "핸드폰 번호는 필수로 입력되어야 합니다.")
    private String phoneNumber;


}
