package org.example.auto_marketing_website.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "facebook_pages")

public class FacebookPage {
    @Id
    private String pageId; // id trang FB
    private String pageName;
    @Column(name = "page_access_token", columnDefinition = "TEXT")
    private String pageAccessToken; // token của trang (để đăng bài)
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
