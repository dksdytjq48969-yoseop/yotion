package com.yo.yotion.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("deleted_at IS NULL")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false)
    private AuthProvider authProvider;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 비밀번호 변경
    public void changePassword(String encodedPassword) {

        // 구글 사용자 비밀번호 변경 차단
        if(this.authProvider != AuthProvider.LOCAL) {
            throw new IllegalArgumentException("구글 로그인 사용자는 비밀번호를 변경할 수 없습니다.");
        }

        this.password = encodedPassword;
    }

    // 프로필 변경(닉네임, 폰번호)
    // 현재 사용자 조회(서비스 레이어) -> Entity에서 파라미터 값 받어 ->
    public void updateProfile(String nickname, String phoneNumber) {

        this.nickname = nickname;
        this.phoneNumber = phoneNumber;

    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
