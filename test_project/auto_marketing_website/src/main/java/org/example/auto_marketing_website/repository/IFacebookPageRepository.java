package org.example.auto_marketing_website.repository;

import org.example.auto_marketing_website.entity.FacebookPage;
import org.example.auto_marketing_website.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IFacebookPageRepository extends JpaRepository<FacebookPage,String> {
    Optional<FacebookPage> findByPageId(String pageId);
    List<FacebookPage> findAllByOwner(User owner);

}
