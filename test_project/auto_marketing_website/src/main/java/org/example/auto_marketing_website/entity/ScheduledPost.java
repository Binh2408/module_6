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
public class ScheduledPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    private LocalDateTime scheduledTime;

    private String platform; // FACEBOOK, WEBSITE, etc.

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "facebook_page_id")
    private FacebookPage facebookPage;
}

