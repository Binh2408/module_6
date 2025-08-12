package org.example.auto_marketing_website.repository;

import org.example.auto_marketing_website.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByFacebookId(String facebookId);
    Optional<User> findByEmail(String email);
}
