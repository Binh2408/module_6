    package org.example.auto_marketing_website.controller;

    import com.fasterxml.jackson.databind.ObjectMapper;
    import org.example.auto_marketing_website.dto.FacebookPageDto;
    import org.example.auto_marketing_website.entity.FacebookPage;
    import org.example.auto_marketing_website.entity.User;
    import org.example.auto_marketing_website.repository.IFacebookPageRepository;
    import org.example.auto_marketing_website.repository.IUserRepository;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Map;
    import java.util.Optional;

    @RestController
    @RequestMapping("/api/facebook")
    public class FacebookController {

        @Autowired
        private IFacebookPageRepository facebookPageRepository;

        @Autowired
        private IUserRepository userRepository;
        private static final Logger logger = LoggerFactory.getLogger(FacebookPageController.class);

        @GetMapping("/pages")
        public ResponseEntity<?> getPages(Authentication authentication) {
            if (authentication == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Chưa đăng nhập"));
            }

            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();

            // Lấy email hoặc id Facebook để tìm user
            String facebookId = (String) attributes.get("id");
            String email = (String) attributes.get("email");

            System.out.println("Facebook ID: " + facebookId);
            System.out.println("Email: " + email);

            // Giả sử bạn lưu user theo email trong DB, bạn có thể tìm theo email
            Optional<User> userOpt = userRepository.findByUsername(email);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "User không tồn tại"));
            }
            User user = userOpt.get();

            List<FacebookPage> pages = facebookPageRepository.findAllByOwner(user);
            return ResponseEntity.ok(Map.of("data", pages));
        }


        @PostMapping("/pages/save")
        public ResponseEntity<?> savePages(
                @RequestBody List<FacebookPageDto> pages,
                Authentication authentication) {
            try {
                String rawJson = new ObjectMapper().writeValueAsString(pages);
                logger.info("=== Raw Pages Payload from FE === {}", rawJson);
            } catch (Exception e) {
                logger.error("Error logging payload", e);
            }

            for (FacebookPageDto dto : pages) {
                logger.info("=== Saving page ===");
                logger.info("ID: {}", dto.getId());
                logger.info("Name: {}", dto.getName());
                logger.info("AccessToken: {}", dto.getAccessToken());
            }

            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
            }

            User user = null;

            if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
                Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();
                String facebookId = (String) attributes.get("id");
                String email = (String) attributes.get("email");
                user = userRepository.findByFacebookId(facebookId)
                        .orElseGet(() -> userRepository.findByEmail(email).orElse(null));
            } else {
                user = userRepository.findByUsername(authentication.getName()).orElse(null);
            }

            if (user == null) {
                return ResponseEntity.status(401).body("User không tồn tại");
            }

            for (FacebookPageDto dto : pages) {
                System.out.println("=== Saving page ===");
                System.out.println("ID: " + dto.getId());
                System.out.println("Name: " + dto.getName());
                System.out.println("AccessToken: " + dto.getAccessToken());
                FacebookPage page = facebookPageRepository.findByPageId(dto.getId())
                        .orElse(new FacebookPage());
                page.setPageId(dto.getId());
                page.setPageName(dto.getName());
                page.setPageAccessToken(dto.getAccessToken());
                page.setOwner(user);
                facebookPageRepository.save(page);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Lưu trang thành công",
                    "pages", pages
            ));

        }


    }

