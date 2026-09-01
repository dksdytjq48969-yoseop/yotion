package com.yo.yotion.domain.post.dto;

import com.yo.yotion.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostListResponseDto {

    // 포스트 리스트를 가져올때 필요한 정보. pageId, postId, title, nickname, createdAt,
    private Long pageId;
    private Long postId;
    private Long userId;
    private String nickname;
    private String title;
    private LocalDateTime createdAt;

    public static PostListResponseDto from(Post post) {
        return PostListResponseDto.builder()
                .postId(post.getId())
                .pageId(post.getPage().getId())
                .nickname(post.getUser().getNickname())
                .title(post.getTitle())
                .createdAt(post.getCreatedAt())
                .build();
    }

}
