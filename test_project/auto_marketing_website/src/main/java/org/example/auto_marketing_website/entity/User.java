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
@Table(name = "users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    // các trường khác...
    @Column(length = 1000)
    private String facebookAccessToken; // access token người dùng lấy từ FB login
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String facebookId;

    private String name;
}
