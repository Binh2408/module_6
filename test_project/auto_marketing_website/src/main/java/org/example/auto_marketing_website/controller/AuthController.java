package org.example.auto_marketing_website.controller;

import org.example.auto_marketing_website.entity.User;
import org.example.auto_marketing_website.repository.IUserRepository;
import org.example.auto_marketing_website.service.FacebookApiService;
import org.example.auto_marketing_website.service.FacebookPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private FacebookApiService facebookApiService;

    @Autowired
    private FacebookPageService facebookPageService;

    @PostMapping("/facebook-login")
    public ResponseEntity<?> facebookLogin(@RequestBody Map<String, String> body) {
        String fbAccessToken = body.get("accessToken");
        if (fbAccessToken == null || fbAccessToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Missing access token"));
        }

        // 1. Lấy thông tin user từ Facebook Graph API
        Map<String, Object> fbUser = facebookApiService.getFacebookUserProfile(fbAccessToken);
        String facebookId = (String) fbUser.get("id");
        String name = (String) fbUser.get("name");
        String email = (String) fbUser.get("email");

        if (facebookId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid Facebook token"));
        }

        // 2. Lưu hoặc cập nhật user
        User user = userRepository.findByFacebookId(facebookId)
                .orElse(new User());
        user.setFacebookId(facebookId);
        user.setName(name);
        user.setEmail(email);
        user.setFacebookAccessToken(fbAccessToken);

        // Nếu chưa có username/password thì tạo mặc định
        if (user.getUsername() == null) {
            user.setUsername(email != null ? email : "fb_" + facebookId);
        }
        if (user.getPassword() == null) {
            user.setPassword("{noop}" + facebookId); // password tạm, có thể random
        }

        userRepository.save(user);

        // 3. Lấy danh sách page user quản lý
        List<Map<String, Object>> pages = facebookApiService.getFacebookPages(fbAccessToken);

        // 4. Lưu page vào DB
        facebookPageService.savePagesForUser(user, pages);

        return ResponseEntity.ok(Map.of(
                "message", "Login thành công",
                "user", user,
                "pages", pages
        ));
    }

}

