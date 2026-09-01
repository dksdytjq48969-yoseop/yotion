package com.yo.yotion.domain.page.dto;

import com.yo.yotion.domain.page.entity.Page;
import com.yo.yotion.domain.post.dto.PostListResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageDetailResponseDto {

    // 첨가해야될것. postlist, pageId, name,
    private Long pageId;
    private String name;
    private String nickname;
    private List<PostListResponseDto> postList;

    public static PageDetailResponseDto from(Page page, List<PostListResponseDto> posts) {
        return PageDetailResponseDto.builder()
                .pageId(page.getId())
                .name(page.getTitle())
                .postList(posts)
                .build();
    }
}
