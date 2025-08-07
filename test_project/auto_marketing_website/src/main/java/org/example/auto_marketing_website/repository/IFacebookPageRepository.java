package org.example.auto_marketing_website.repository;

import org.example.auto_marketing_website.entity.FacebookPage;
import org.example.auto_marketing_website.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFacebookPageRepository extends JpaRepository<FacebookPage,Long> {
    List<FacebookPage> findByUser(User user);
}
