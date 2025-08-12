package org.example.auto_marketing_website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "scheduled_posts")

public class ScheduledPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 2000)
    private String message;

    private String imageUrl; // optional

    private LocalDateTime scheduledTime;

    @Enumerated(EnumType.STRING)
    private PostStatus status = PostStatus.PENDING;

    @Column(length = 2000)
    private String failedReason;

    @ManyToOne
    @JoinColumn(name = "page_id")
    private FacebookPage page;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Platform platform = Platform.FACEBOOK;
}
