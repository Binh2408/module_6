package org.example.auto_marketing_website.service;

import org.example.auto_marketing_website.entity.ScheduledPost;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class FacebookPostService {
    private final RestTemplate restTemplate = new RestTemplate();

    public void publish(ScheduledPost post) {
        String pageId = post.getFacebookPage().getPageId();
        String accessToken = post.getFacebookPage().getAccessToken();
        String url = "https://graph.facebook.com/" + pageId + "/photos";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("caption", post.getContent());
        body.add("url", post.getImageUrl());
        body.add("access_token", accessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        System.out.println("📡 Phản hồi từ Facebook: " + response.getBody());

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Đăng bài thất bại: " + response.getBody());
        }
    }

}
