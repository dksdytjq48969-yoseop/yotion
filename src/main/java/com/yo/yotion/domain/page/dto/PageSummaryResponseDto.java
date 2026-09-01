package com.yo.yotion.domain.page.dto;

import com.yo.yotion.domain.page.entity.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PageSummaryResponseDto {

    private Long pageId;
    private String name;

    public static PageSummaryResponseDto from(Page page){
        return PageSummaryResponseDto.builder()
                .pageId(page.getId())
                .name(page.getTitle())
                .build();
    }

}
