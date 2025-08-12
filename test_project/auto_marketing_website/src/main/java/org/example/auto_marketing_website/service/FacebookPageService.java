package org.example.auto_marketing_website.service;

import org.example.auto_marketing_website.entity.FacebookPage;
import org.example.auto_marketing_website.entity.User;
import org.example.auto_marketing_website.repository.IFacebookPageRepository;
import org.example.auto_marketing_website.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FacebookPageService {

    @Autowired
    private IFacebookPageRepository facebookPageRepository;

    @Autowired
    private IUserRepository userRepository;

    public void savePagesForUser(User user, List<Map<String, Object>> pages) {
        for (Map<String, Object> p : pages) {
            FacebookPage page = new FacebookPage();
            page.setPageId((String) p.get("id"));
            page.setPageName((String) p.get("name"));
            page.setPageAccessToken((String) p.get("access_token"));
            page.setOwner(user);
            facebookPageRepository.save(page);
        }
    }

}
