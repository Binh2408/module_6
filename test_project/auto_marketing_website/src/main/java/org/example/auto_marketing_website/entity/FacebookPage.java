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
public class FacebookPage {
    @Id
    private String pageId; // chính là ID của trang Facebook

    private String name;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String accessToken;

    @ManyToOne
    private User user;
}

