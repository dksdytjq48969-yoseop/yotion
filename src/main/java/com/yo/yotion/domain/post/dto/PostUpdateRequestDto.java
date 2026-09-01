package com.yo.yotion.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequestDto {

    @NotBlank(message = "제목 입력은 필수입니다.")
    private String title;

    private String content;

    private String imageUrl;

}
