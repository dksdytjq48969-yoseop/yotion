package com.yo.yotion.domain.page.controller;

import com.yo.yotion.domain.page.dto.PageCreateRequestDto;
import com.yo.yotion.domain.page.dto.PageDetailResponseDto;
import com.yo.yotion.domain.page.dto.PageSummaryResponseDto;
import com.yo.yotion.domain.page.dto.PageUpdateRequestDto;
import com.yo.yotion.domain.page.service.PageService;
import com.yo.yotion.domain.user.entity.User;
import com.yo.yotion.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pages")
public class PageController {

    private final PageService pageService;

    // 1. 페이지 생성
    @PostMapping
    public ResponseEntity<PageSummaryResponseDto> createPage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PageCreateRequestDto requestDto) {

        PageSummaryResponseDto response = pageService.createPage(userDetails.getId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // 2. 내 페이지 목록 조회
    @GetMapping
    public ResponseEntity<List<PageSummaryResponseDto>> getMyPages(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<PageSummaryResponseDto> response = pageService.getMyPages(userDetails.getId());
        return ResponseEntity.ok(response);
    }


    // 3. 페이지 상세 조회
    @GetMapping("/{pageId}")
    public ResponseEntity<PageDetailResponseDto> getPageDetail(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long pageId) {

        PageDetailResponseDto response = pageService.getPageDetail(userDetails.getId(), pageId);
        return ResponseEntity.ok(response);

    }

    // 4. 페이지 수정
    @PutMapping("/{pageId}")
    public ResponseEntity<PageSummaryResponseDto> updatePage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long pageId,
            @Valid @RequestBody PageUpdateRequestDto requestDto) {

        PageSummaryResponseDto response = pageService.updatePage(userDetails.getId(), pageId, requestDto);
        return ResponseEntity.ok(response);
    }

    // 5. 페이지 삭제
    @DeleteMapping("/{pageId}")
    public ResponseEntity<Void> deletePage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long pageId) {

        pageService.deletePage(userDetails.getId(), pageId);
        return ResponseEntity.noContent().build();
    }
}
