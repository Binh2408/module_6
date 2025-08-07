package org.example.auto_marketing_website.controller;

import org.example.auto_marketing_website.dto.FacebookPageDto;
import org.example.auto_marketing_website.entity.FacebookPage;
import org.example.auto_marketing_website.entity.User;
import org.example.auto_marketing_website.service.FacebookPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/facebook-pages")
public class FacebookPageController {
    private FacebookPageService facebookPageService;

    public FacebookPageController(FacebookPageService facebookPageService) {
        this.facebookPageService = facebookPageService;
    }
    // ⚠️ Tạm hardcode user (bạn nên lấy từ SecurityContext hoặc Session thực tế)
    private User getMockUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("demo");
        return user;
    }

    @PostMapping("/sync")
    public List<FacebookPage> syncPages(@RequestParam("userAccessToken") String userAccessToken) {
        return facebookPageService.fetchAndSavePages(userAccessToken, getMockUser());
    }

    @GetMapping
    public List<FacebookPage> getPagesForUser() {
        return facebookPageService.getPagesByUser(getMockUser());
    }

    @PostMapping("/save")
    public ResponseEntity<?> savePage(@RequestBody FacebookPageDto dto) {
        facebookPageService.save(dto.getPageId(), dto.getAccessToken(), dto.getName(), getMockUser());
        return ResponseEntity.ok("Đã lưu page info");
    }
}
