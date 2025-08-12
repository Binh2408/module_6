package org.example.auto_marketing_website.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class FacebookPageController {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/api/facebook/pages-list")
    public ResponseEntity<?> getUserFacebookPages(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());

        if (authorizedClient == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String accessToken = authorizedClient.getAccessToken().getTokenValue();

        // Gọi Facebook Graph API để lấy danh sách pages
        String url = "https://graph.facebook.com/v17.0/me/accounts?access_token=" + accessToken;

        try {
            // Dùng RestTemplate hoặc WebClient để gọi API Facebook
            RestTemplate restTemplate = new RestTemplate();
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch pages from Facebook");
        }
    }
}
