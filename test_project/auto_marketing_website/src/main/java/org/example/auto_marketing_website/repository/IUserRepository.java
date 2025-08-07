package org.example.auto_marketing_website.repository;

import jdk.jfr.Registered;
import org.example.auto_marketing_website.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

@Registered
public interface IUserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);
}
