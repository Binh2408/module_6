package org.example.auto_marketing_website.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class FacebookApiService {
    private final Logger logger = LoggerFactory.getLogger(FacebookApiService.class);
    private final RestTemplate restTemplate;

    public FacebookApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    public List<Map<String, Object>> getFacebookPages(String userAccessToken) {
        String url = "https://graph.facebook.com/me/accounts?access_token=" + userAccessToken;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("data")) {
            throw new RuntimeException("Lấy danh sách page lỗi");
        }
        return (List<Map<String, Object>>) response.get("data");
    }

    /**
     * Đăng bài lên page Facebook
     */
    public String postToFacebookPage(String pageId, String message, String pageAccessToken) {
        String url = String.format("https://graph.facebook.com/%s/feed", pageId);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("message", message);
        params.add("access_token", pageAccessToken);

        Map<?, ?> response = restTemplate.postForObject(url, params, Map.class);

        if (response != null && response.containsKey("id")) {
            return (String) response.get("id");
        }
        throw new RuntimeException("Không đăng bài lên Facebook được");
    }

    public Map<String, Object> getFacebookUserProfile(String accessToken) {
        String url = "https://graph.facebook.com/me?fields=id,name,email&access_token=" + accessToken;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, Map.class);
    }

    // Post text-only to /{pageId}/feed
    public String postTextToPage(String pageId, String pageAccessToken, String message){
        String url = "https://graph.facebook.com/" + pageId + "/feed";
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("message",message);
        params.add("access_token",pageAccessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params,headers);
        ResponseEntity<Map> resp = restTemplate.postForEntity(url,request,Map.class);
        logger.info("Facebook postText response: status={}, body={}", resp.getStatusCode(), resp.getBody());
        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody()!=null && resp.getBody().containsKey("id")) {
            return String.valueOf(resp.getBody().get("id"));
        }
        throw new RuntimeException("Facebook post failed: " + (resp.getBody()!=null ? resp.getBody() : resp.getStatusCode()));
    }

    // Post with image (remote URL) to /{pageId}/photos
    public String postImageToPage(String pageId, String pageAccessToken, String message, String imageUrl) {
        String url = "https://graph.facebook.com/" + pageId + "/photos";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("url", imageUrl);
        params.add("caption", message);
        params.add("access_token", pageAccessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> resp = restTemplate.postForEntity(url, request, Map.class);
        logger.info("Facebook postImage response: status={}, body={}", resp.getStatusCode(), resp.getBody());
        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody()!=null && resp.getBody().containsKey("id")) {
            return String.valueOf(resp.getBody().get("id"));
        }
        throw new RuntimeException("Facebook image post failed: " + (resp.getBody()!=null ? resp.getBody() : resp.getStatusCode()));
    }
}
