package com.yo.yotion.domain.member.entity;

import com.yo.yotion.domain.page.entity.Page;
import com.yo.yotion.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invitation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invitation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inviter_member_id", nullable = false)
    private Member inviterMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitee_user_id", nullable = false)
    private User inviteeUser;

    // 제안 받은 포지션, 초대 상태, createdat, 메세지, 만료
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole proposedRole;

    @Column(nullable = false, unique = true)
    private String token;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime expiredAt;

    @Builder
    private Invitation(Page page, Member inviterMember, User inviteeUser, MemberRole proposedRole) {
        this.page = page;
        this.inviterMember = inviterMember;
        this.inviteeUser = inviteeUser;
        this.status = InvitationStatus.PENDING;
        this.proposedRole = proposedRole;
        this.token = UUID.randomUUID().toString();
        this.expiredAt = LocalDateTime.now().plusDays(7); // 7일 후 만료.
    }

    public static Invitation create(Page page, Member inviterMember, User inviteeUser, MemberRole proposedRole) {
        return Invitation.builder()
                .page(page)
                .inviterMember(inviterMember)
                .inviteeUser(inviteeUser)
                .proposedRole(proposedRole)
                .build();
    }

    public boolean isValid() {
        return this.status == InvitationStatus.PENDING && !isExpired();
    }

    private boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiredAt);
    }




}
