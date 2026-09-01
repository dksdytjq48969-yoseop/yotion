package com.yo.yotion.domain.page.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageUpdateRequestDto {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 20, message = "제목은 최대 20자입니다.")
    private String title;

}
