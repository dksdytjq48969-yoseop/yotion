package com.yo.yotion.domain.post.entity;

import com.yo.yotion.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_edit_session",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"post_id","user_id"})
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostEditSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_edit_session_id")
    private Long postEditSessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "cursor_position")
    private Integer cursorPosition;

    @Column(name = "selection_start")
    private Integer selectionStart;

    @Column(name = "selection_end")
    private Integer selectionEnd;

    @Column(nullable = false)
    @Builder.Default
    private String color = "#4285F4";

    @UpdateTimestamp
    @Column(name = "last_active_at", nullable = false)
    private LocalDateTime lastActiveAt;
}
