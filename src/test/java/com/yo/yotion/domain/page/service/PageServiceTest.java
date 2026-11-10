package com.yo.yotion.domain.page.service;

import com.yo.yotion.domain.member.entity.Member;
import com.yo.yotion.domain.member.entity.MemberRole;
import com.yo.yotion.domain.member.repository.MemberRepository;
import com.yo.yotion.domain.page.dto.PageCreateRequestDto;
import com.yo.yotion.domain.page.dto.PageSummaryResponseDto;
import com.yo.yotion.domain.page.entity.Page;
import com.yo.yotion.domain.page.repository.PageRepository;
import com.yo.yotion.domain.user.entity.AuthProvider;
import com.yo.yotion.domain.user.entity.User;
import com.yo.yotion.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PageServiceTest {

    @Autowired
    private PageService pageService;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 페이지_생성시_페이지와_owner멤버가_저장된다() {
        //준비
        User user = userRepository.save(User.builder()
                .email("page-test@example.com")
                .authProvider(AuthProvider.LOCAL)
                .nickname("test")
                .build());

        PageCreateRequestDto requestDto = new PageCreateRequestDto();
        requestDto.title = "테스트 페이지";

        //실행
        PageSummaryResponseDto response =
                pageService.createPage(user.getId(), requestDto);

        // 응답 검증
        assertNotNull(response.getPageId());
        assertEquals("테스트 페이지", response.getName());

        // 변경 내용을 DB에 반영, 캐시 대신 DB에서 조회
        entityManager.flush();
        entityManager.clear();

        // 페이지 저장 검증
        Page savedPage = pageRepository.findById(response.getPageId())
                .orElseThrow();

        assertEquals("테스트 페이지", savedPage.getTitle());
        assertEquals(user.getId(), savedPage.getUser().getId());

        // 생성자의 ONWER 검증
        Member ownerMember = memberRepository
                .findByPage_IdAndUser_Id(savedPage.getId(), user.getId())
                .orElseThrow();

        assertEquals(MemberRole.OWNER, ownerMember.getRole());
    }

    @Test
    void 존재하지_않는_사용자는_페이지를_생성할_수_없다() {
        // 기존 테스트 데이터와 겹치지 않는 미존재 ID 확인
        Long missingUserId = Long.MAX_VALUE;
        assertFalse(userRepository.existsById(missingUserId));

        long pageCountBefore = pageRepository.count();
        long memberCountBefore = memberRepository.count();

        PageCreateRequestDto requestDto = new PageCreateRequestDto();
        requestDto.title = "생성 불가 페이지";

        // 실행 및 예외 검증
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pageService.createPage(missingUserId, requestDto)
        );

        assertEquals("존재하지 않는 사용자입니다.", exception.getMessage());

        // 실패한 요청으로 데이터가 추가되지 않았는지 검증
        entityManager.flush();
        entityManager.clear();

        assertEquals(pageCountBefore, pageRepository.count());
        assertEquals(memberCountBefore, memberRepository.count());


    }

}
