package com.yo.yotion.domain.member.entity;


import com.yo.yotion.domain.page.entity.Page;
import com.yo.yotion.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "member",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"user_id", "page_id"})
        }
)
@Getter
@NoArgsConstructor
@SQLRestriction("deleted_at IS NULL")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private Page page;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role = MemberRole.EDITOR;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    private Member(User user, Page page, MemberRole role) {
        this.user = user;
        this.page = page;
        this.role = role;
    }

    public static Member create(User user, Page page, MemberRole role) {
        return Member.builder()
                .user(user)
                .page(page)
                .role(role)
                .build();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
