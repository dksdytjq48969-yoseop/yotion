package com.yo.yotion.domain.member.service;

import com.yo.yotion.domain.member.dto.MemberResponseDto;
import com.yo.yotion.domain.member.entity.*;
import com.yo.yotion.domain.member.repository.InvitationRepository;
import com.yo.yotion.domain.member.repository.MemberRepository;
import com.yo.yotion.domain.page.entity.Page;
import com.yo.yotion.domain.page.repository.PageRepository;
import com.yo.yotion.domain.user.entity.User;
import com.yo.yotion.domain.user.repository.UserRepository;
import com.yo.yotion.global.exception.BusinessException;
import com.yo.yotion.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Member 구현해야 되는 서비스 로직 - Member 초대, 삭제, 권한수정, 조회
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final PageRepository pageRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final InvitationRepository invitationRepository;

    // 1. 페이지 멤버 전체 조회
    public List<MemberResponseDto> getMembers (Long pageId, Long userId){
        // 1) page 존재 확인
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAGE_NOT_FOUND));

        // 2) 멤버인지 확인
        if(!memberRepository.existsByPage_IdAndUser_Id(pageId, userId)) {
            throw new BusinessException(ErrorCode.NOT_PAGE_MEMBER);
        }

        // 3) 멤버리스트 반환
        return memberRepository.findByPage_Id(pageId)
                .stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());
    }

    // 2. 페이지 멤버 초대
    // 멤버 초대는 Request인가 Response인가. 변동이 있으니까 Request가 맞는것 같다. 근데 초대가 안되어있을때도 Request인가? 수락하는 방식을 만들어야
    // 할것 같은데. 수락하는 방식은 어케 만들지? 서버에 누구 초청할건지 요청 -> 요청 대상에게 초청 전달 -> 수락 or 거절 -> 수락시 Member 아닐시 끝.
    @Transactional
    public void inviteMember(Long pageId, Long userId, Long targetUserId, MemberRole memberRole) {

        // 1) page 존재 확인
        Page page = pageRepository.findById(pageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAGE_NOT_FOUND));

        // 2) inviter가 멤버이며 viewer는 아닌지 확인
        Member member_inviter = memberRepository.findByPage_IdAndUser_Id(pageId, userId)
                .orElseThrow(()-> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        if(member_inviter.getRole() == MemberRole.VIEWER) {
            throw new BusinessException(ErrorCode.NO_INVITING_AUTHORIZATION);
        }

        // 3) invitee 유저 확인

            // (1) 존재하는 유저인지 확인
        User user_invitee = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            // (2) 멤버인지 확인
        if (memberRepository.existsByPage_IdAndUser_Id(pageId, targetUserId)) {
            throw new BusinessException(ErrorCode.ALREADY_MEMBER);
        }

            // (3) 이미 초대됐는지 확인
        if(invitationRepository.existsByPage_IdAndInviteeUser_IdAndStatus(pageId, targetUserId, InvitationStatus.PENDING)){
          throw new BusinessException(ErrorCode.ALREADY_INVITED);
        }

        // 4) 초대 전송 로직.
        // 실제 초대 전송 로직. invitation 객체 만든다 -> 전달 대상에게 이메일을 보낸다. -> 이메일을 받은 대상은 초대 링크를 클릭하면 자동으로 멤버로 가입된다.

        // 객체 생성
        Invitation invitation = Invitation.create(page, member_inviter, user_invitee, memberRole);
        invitationRepository.save(invitation);

        eventPublisher.publishEvent(new InvitationCreatedEvent(invitation));

    }

}
