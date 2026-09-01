package com.yo.yotion.domain.post.entity;

import com.yo.yotion.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_operation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_operation_id")
    private Long postOperationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "post_operation_type", nullable = false)
    private PostOperationType postOperationType;

    @Column(name = "postion", nullable = false)
    private Integer position;

    @Column(name = "length")
    private Integer length;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


}
