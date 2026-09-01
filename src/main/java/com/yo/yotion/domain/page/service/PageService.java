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
import com.yo.yotion.domain.post.dto.PostListResponseDto;
import com.yo.yotion.domain.post.entity.Post;
import com.yo.yotion.domain.post.repository.PostRepository;
import com.yo.yotion.domain.user.entity.User;
import com.yo.yotion.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PageService {

    private final PageRepository pageRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;


    // 1. 페이지 생성
    @Transactional
    public PageSummaryResponseDto createPage(Long userId, PageCreateRequestDto requestDto) {

        // 1) 유저 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2) 페이지 생성
        Page page = Page.create(user, requestDto.getTitle());
        pageRepository.save(page);

        // 3) 생성자를 owner로 Member등록
        Member ownerMember = Member.create(user, page, MemberRole.OWNER);
        memberRepository.save(ownerMember);

        return PageSummaryResponseDto.from(page);
    }

    // 2. 내 페이지 목록 조회
    // 흐름 내 페이지 알려면? 내 정보 userId -> Page 조회(userId로)
    public List<PageSummaryResponseDto> getMyPages(Long userId) {

        List<Member> members = memberRepository.findByUser_Id(userId);

        return members.stream()
                .map(member -> PageSummaryResponseDto.from(member.getPage()))
                .collect(Collectors.toList());
    }


    // 3. 상세페이지 조회
    public PageDetailResponseDto getPageDetail(Long userId, Long pageId) {

        // 1) 페이지 존재 확인
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new IllegalArgumentException("페이지가 존재하지 않습니다."));

        // 2) Member인지 확인
        memberRepository.findByPage_IdAndUser_Id(pageId, userId)
                .orElseThrow(() -> new IllegalArgumentException("페이지 멤버가 아닙니다."));

        // 3) 응답 생성 (Post목록은 추후 PostService 생성시 채움) ******
        List<PostListResponseDto> posts = Collections.emptyList();

        // 반환
        return PageDetailResponseDto.from(page, posts);
    }


    // 4. 페이지 수정ㅒ Owner, Admin만 가능
    @Transactional
    public PageSummaryResponseDto updatePage(Long userId, Long pageId, PageUpdateRequestDto requestDto) {
        // 1) 페이지 존재 확인
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new IllegalArgumentException("페이지가 존재하지 않습니다."));

        // 2) 권한 확인 유저
        // 멤버인지 / 멤버인데 admin owner가 맞는지
        Member member = memberRepository.findByPageIdAndUserIdAndRoleIn(pageId, userId,
                List.of(MemberRole.OWNER, MemberRole.ADMIN))
                .orElseThrow(() -> new IllegalArgumentException("수정할 권한이 없습니다."));

        // 3) 수정 프로세스
        page.updateTitle(requestDto.getTitle());

        return PageSummaryResponseDto.from(page);


    }


    // 5. 페이지 삭제
    // 뭐 페이지 존재 확인 -> owner인지 확인 -> 삭제
    @Transactional
    public void deletePage(Long userId, Long pageId) {

        // 1) 페이지 존재 확인
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new IllegalArgumentException("페이지가 존재하지 않습니다."));

        // 2) 유저가 owner인지 확인
        memberRepository.findByPageIdAndUserIdAndRoleIn(pageId, userId,
                List.of(MemberRole.OWNER))
                .orElseThrow(() -> new IllegalArgumentException("삭제 권한이 없습니다."));

        // 3) Post soft delete
        List<Post> posts = postRepository.findByPage_Id(pageId);
        posts.forEach(Post::delete);

        // 4) Member soft delete
        List<Member> members = memberRepository.findByPage_Id(pageId);
        members.forEach(Member::delete);

        // 5) Page delete
        page.delete();
    }

}
