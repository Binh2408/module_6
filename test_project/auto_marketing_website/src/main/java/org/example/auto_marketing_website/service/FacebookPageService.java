package org.example.auto_marketing_website.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.auto_marketing_website.entity.FacebookPage;
import org.example.auto_marketing_website.entity.User;
import org.example.auto_marketing_website.repository.IFacebookPageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class FacebookPageService implements IFacebookPageService {
    private IFacebookPageRepository facebookPageRepository;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    public FacebookPageService(IFacebookPageRepository facebookPageRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.facebookPageRepository = facebookPageRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(String pageId, String accessToken, String name, User user) {
        FacebookPage page = new FacebookPage();
        page.setPageId(pageId);
        page.setAccessToken(accessToken);
        page.setName(name);
        page.setUser(user);
        facebookPageRepository.save(page);
    }

    @Override
    public List<FacebookPage> fetchAndSavePages(String userAccessToken, User user) {
        String url = "https://graph.facebook.com/v19.0/me/accounts?access_token=" + userAccessToken;
        String response = restTemplate.getForObject(url,String.class);

        List<FacebookPage> savedPages = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(response);
            JsonNode data = root.get("data");
            if (data != null && data.isArray()) {
                for (JsonNode pageNode : data) {
                    FacebookPage page = new FacebookPage();
                    page.setPageId(pageNode.get("id").asText());
                    page.setName(pageNode.get("name").asText());
                    page.setAccessToken(pageNode.get("access_token").asText());
                    page.setUser(user);
                    savedPages.add(facebookPageRepository.save(page));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse or save Facebook pages: " + e.getMessage(), e);
        }

        return savedPages;
    }

    @Override
    public List<FacebookPage> getPagesByUser(User user) {
        return facebookPageRepository.findByUser(user);
    }

    @Override
    public FacebookPage getById(Long pageId) {
        return facebookPageRepository.findById(pageId).orElse(null);
    }
}
