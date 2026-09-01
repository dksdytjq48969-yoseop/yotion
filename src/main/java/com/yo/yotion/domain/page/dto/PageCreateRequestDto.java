package com.yo.yotion.domain.page.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageCreateRequestDto {

    @NotBlank(message = "페이지 이름은 필수입니다.")
    @Size(max = 20, message = "제목은 최대 20자입니다.")
    public String title;

}
