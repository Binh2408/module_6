package org.example.auto_marketing_website.service;

import org.example.auto_marketing_website.entity.FacebookPage;
import org.example.auto_marketing_website.entity.User;

import java.util.List;

public interface IFacebookPageService {
    List<FacebookPage> fetchAndSavePages(String userAccessToken, User user);
    List<FacebookPage> getPagesByUser(User user);
    FacebookPage getById(Long pageId);
    void save(String pageId, String accessToken, String name, User user);

}
