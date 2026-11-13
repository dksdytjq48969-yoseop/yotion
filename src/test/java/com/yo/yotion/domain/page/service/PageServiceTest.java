package com.yo.yotion.domain.page.service;

import com.yo.yotion.domain.member.entity.Member;
import com.yo.yotion.domain.member.entity.MemberRole;
import com.yo.yotion.domain.member.repository.MemberRepository;
import com.yo.yotion.domain.page.dto.PageCreateRequestDto;
import com.yo.yotion.domain.page.dto.PageDetailResponseDto;
import com.yo.yotion.domain.page.dto.PageSummaryResponseDto;
import com.yo.yotion.domain.page.dto.PageUpdateRequestDto;
import com.yo.yotion.domain.page.entity.Page;
import com.yo.yotion.domain.page.repository.PageRepository;
import com.yo.yotion.domain.user.entity.AuthProvider;
import com.yo.yotion.domain.user.entity.User;
import com.yo.yotion.domain.user.repository.UserRepository;
import com.yo.yotion.global.exception.BusinessException;
import com.yo.yotion.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

    // 유저 생성 메서드 - return 값 User 객체
    private User saveTestUser(String name) {
        return userRepository.save(User.builder()
                .email(name + "@example.com")
                .nickname(name)
                .authProvider(AuthProvider.LOCAL)
                .build());
    }

    // 페이지 생성 메서드 - return 값 Page id
    public Long createTestPage(User ownerUser) {
        PageCreateRequestDto requestDto = new PageCreateRequestDto();
        requestDto.title = "기존 제목";

        return pageService.createPage(ownerUser.getId(), requestDto).getPageId();
    }

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

    @Test
    void 페이지_멤버만_상세_조회_가능 () {
        // 준비
        User user = userRepository.save(User.builder()
                        .email("owner@example.com")
                        .nickname("페이지소유자")
                        .authProvider(AuthProvider.LOCAL)
                        .build()
        );

        User viewerUser = userRepository.save(User.builder()
                        .email("viewer@example.com")
                        .nickname("viewer")
                        .authProvider(AuthProvider.LOCAL)
                        .build()
        );

        PageCreateRequestDto requestDto = new PageCreateRequestDto();
        requestDto.title = "조회 테스트 페이지";

        PageSummaryResponseDto createdPage =
                pageService.createPage(user.getId(), requestDto);

        Page page = pageRepository.findById(createdPage.getPageId())
                .orElseThrow();

        memberRepository.save(
                    Member.create(viewerUser, page,MemberRole.VIEWER)
        );

        entityManager.flush();
        entityManager.clear();

        // 실행
        PageDetailResponseDto response = pageService.getPageDetail(viewerUser.getId(), createdPage.getPageId());

        // 검증
        assertEquals(createdPage.getPageId(), response.getPageId());
        assertEquals("조회 테스트 페이지", response.getName());
    }

    @Test
    void 페이지_비멤버는_상세_조회가_불가하다 () {

        // 준비
        User user = userRepository.save(User.builder()
                .email("owner@example.com")
                .nickname("페이지소유자")
                .authProvider(AuthProvider.LOCAL)
                .build());

        User nonMemberUser = userRepository.save(User.builder()
                .email("non-member@example.com")
                .nickname("외부인")
                .authProvider(AuthProvider.LOCAL)
                .build());

        PageCreateRequestDto requestDto = new PageCreateRequestDto();
        requestDto.title = "생성된페이지";

        PageSummaryResponseDto createdPage = pageService.createPage(user.getId(),requestDto);

        Page page = pageRepository.findById(createdPage.getPageId())
                .orElseThrow();

        entityManager.flush();
        entityManager.clear();


        // 실행 및 검증
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pageService.getPageDetail(
                        nonMemberUser.getId(),
                        createdPage.getPageId()
                )

        );

        assertEquals("페이지 멤버가 아닙니다.", exception.getMessage());
    }

    // 수정 테스트
    @ParameterizedTest
    @EnumSource(
            value = MemberRole.class,
            names = {"OWNER", "ADMIN"}
    )
    // 유저 생성 -> 페이지 생성 -> 페이지 수정 시도
    void OWNER와_ADMIN은_페이지_수정_가능 (MemberRole role) {

        // 유저와 페이지id 생성
        User ownerUser = saveTestUser("owner");
        Long pageId = createTestPage(ownerUser);

        User modifyingUser = ownerUser;

        // 페이지에 어드민으로 등록
        if(role == MemberRole.ADMIN) {
            modifyingUser = saveTestUser("admin");

            Page page = pageRepository.findById(pageId).orElseThrow();
            memberRepository.save(
                    Member.create(modifyingUser, page, MemberRole.ADMIN)
            );

        }

        entityManager.flush();
        entityManager.clear();

        PageUpdateRequestDto requestDto = PageUpdateRequestDto.builder()
                .title("변경된 제목")
                .build();

        // 실행

        PageSummaryResponseDto responseDto = pageService.updatePage(modifyingUser.getId(), pageId, requestDto);

        entityManager.flush();
        entityManager.clear();

        // id는 그대로인지, 제목은 변경됐는지 확인.
        assertEquals(pageId, responseDto.getPageId());
        assertEquals("변경된 제목", responseDto.getName());

        Page updatedPage = pageRepository.findById(pageId).orElseThrow();
        assertEquals("변경된 제목", updatedPage.getTitle());

    }

    @ParameterizedTest
    @EnumSource(
            value = MemberRole.class,
            names = {"EDITOR", "VIEWER"}
    )
    void EDITOR와_VIEWER는_페이지를_수정할수없다 (MemberRole role) {

        // 준비. 유저, 페이지 생성 -> 유저에게 권한 부여 -> 페이지 수정 시도 -> 페이지 수정 됐는지 확인

        User ownerUser = saveTestUser("OWNER");
        User cantChangeUser = saveTestUser("EDITOR");
        Long pageId = createTestPage(ownerUser);
        Page createdPage = pageRepository.findById(pageId).orElseThrow();

        memberRepository.save(
                Member.create(cantChangeUser, createdPage, role)
        );

        entityManager.flush();
        entityManager.clear();

        PageUpdateRequestDto requestDto = PageUpdateRequestDto.builder()
                .title("바뀐 제목")
                .build();

        // 실행 및 예외 검증
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> pageService.updatePage(cantChangeUser.getId(), pageId, requestDto)
        );

        assertEquals(
                ErrorCode.NO_UPDATING_AUTHORIZATION,
                exception.getErrorCode()
        );

        entityManager.flush();
        entityManager.clear();

        Page unChangedPage =
                pageRepository.findById(pageId).orElseThrow();
        assertEquals("기존 제목", unChangedPage.getTitle());

    }




}
