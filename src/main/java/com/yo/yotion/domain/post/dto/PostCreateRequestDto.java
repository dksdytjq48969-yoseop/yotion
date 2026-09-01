package com.yo.yotion.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCreateRequestDto {

    @NotNull(message = "페이지 ID는 필수입니다.")
    private Long pageId;

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    private String content;

    private String imageUrl;

}
