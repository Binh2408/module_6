package org.example.auto_marketing_website.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.auto_marketing_website.dto.FacebookPageDto;
import org.example.auto_marketing_website.entity.FacebookPage;
import org.example.auto_marketing_website.entity.User;
import org.example.auto_marketing_website.repository.IFacebookPageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FacebookService {

    private final RestTemplate restTemplate;

    public FacebookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<FacebookPageDto> getUserPages(String userAccessToken) {
        String url = "https://graph.facebook.com/v21.0/me/accounts"
                + "?fields=id,name,access_token"
                + "&access_token=" + userAccessToken;

        ResponseEntity<JsonNode> response = restTemplate.getForEntity(url, JsonNode.class);
        JsonNode data = response.getBody().get("data");

        List<FacebookPageDto> pages = new ArrayList<>();
        if (data != null && data.isArray()) {
            for (JsonNode page : data) {
                FacebookPageDto dto = new FacebookPageDto();
                dto.setId(page.get("id").asText());
                dto.setName(page.get("name").asText());
                dto.setAccessToken(page.get("access_token").asText());
                pages.add(dto);
            }
        }
        return pages;
    }
}
