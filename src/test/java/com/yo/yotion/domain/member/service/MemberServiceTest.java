package com.yo.yotion.domain.member.service;

import com.yo.yotion.domain.member.entity.Invitation;
import com.yo.yotion.domain.member.entity.InvitationStatus;
import com.yo.yotion.domain.member.entity.Member;
import com.yo.yotion.domain.member.entity.MemberRole;
import com.yo.yotion.domain.member.repository.InvitationRepository;
import com.yo.yotion.domain.member.repository.MemberRepository;
import com.yo.yotion.domain.page.dto.PageCreateRequestDto;
import com.yo.yotion.domain.page.entity.Page;
import com.yo.yotion.domain.page.repository.PageRepository;
import com.yo.yotion.domain.page.service.PageService;
import com.yo.yotion.domain.user.entity.AuthProvider;
import com.yo.yotion.domain.user.entity.User;
import com.yo.yotion.domain.user.repository.UserRepository;
import com.yo.yotion.global.exception.BusinessException;
import com.yo.yotion.global.exception.ErrorCode;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
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
public class MemberServiceTest {

    // 멤버와 관련된. UserRepository, PageRepository,
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PageService pageService;

    @Autowired
    private MemberService memberService;

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
    void 초대가_정상적으로_저장됐는지_확인 () {

        // 준비
        // 변수 - ownerUser, invitingTargetUser, createdPage
        // 로직순서 - 기본 재료 생성 -> ownerUser가 invitingTargetUser 초대 -> inviting 생성 -> inviting 생성 확인.
        User ownerUser = saveTestUser("ownerUser");
        User invitingTargetUser = saveTestUser("invitingTargetUser");
        Long pageId = createTestPage(ownerUser);
        Page createdPage = pageRepository.findById(pageId).orElseThrow();

        entityManager.flush();
        entityManager.clear();

        // 멤버 초대 시도
        // inviteMember 메소드는 반환하는게 업슨ㄴ데 어케 초대장 조회함? -> repository 확인
        memberService.inviteMember(pageId,ownerUser.getId(),invitingTargetUser.getId(),MemberRole.VIEWER);

        // invite
        assertTrue(
                invitationRepository.existsByPage_IdAndInviteeUser_IdAndStatus(
                        pageId,invitingTargetUser.getId(), InvitationStatus.PENDING
                )
        );
    }


    @Test
    void Member중_viewer는_초대할수없다 () {
        //준비 - ownerUser, viewerUser, testPage, viewerMember, inviteTargetUser

        User ownerUser = saveTestUser("ownerUser");
        User viewerUser = saveTestUser("viewerUser");
        User inviteTargetUser = saveTestUser("inviteTargetUser");
        Long pageId = createTestPage(ownerUser);
        Page createdPage = pageRepository.findById(pageId).orElseThrow();

        Member viewerMember = memberRepository.save(Member.builder()
                .role(MemberRole.VIEWER)
                .user(viewerUser)
                .page(createdPage)
                .build());

        entityManager.flush();
        entityManager.clear();

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> memberService.inviteMember(pageId,viewerUser.getId(), inviteTargetUser.getId(), MemberRole.VIEWER)
        );

        entityManager.flush();
        entityManager.clear();

        assertEquals(ErrorCode.NO_INVITING_AUTHORIZATION, exception.getErrorCode());
    }
}
