package org.example.auto_marketing_website.controller;

import org.example.auto_marketing_website.dto.ScheduledPostDto;
import org.example.auto_marketing_website.entity.FacebookPage;
import org.example.auto_marketing_website.entity.PostStatus;
import org.example.auto_marketing_website.entity.ScheduledPost;
import org.example.auto_marketing_website.entity.User;
import org.example.auto_marketing_website.repository.IUserRepository;
import org.example.auto_marketing_website.service.ScheduledPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/schedule")
public class ScheduledPostController {

    private ScheduledPostService scheduledPostService;
    private IUserRepository userRepository;

    public ScheduledPostController(ScheduledPostService scheduledPostService, IUserRepository userRepository) {
        this.scheduledPostService = scheduledPostService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> schedulePost(@RequestBody ScheduledPost post) {
        scheduledPostService.schedulePost(post);
        return ResponseEntity.ok("Post scheduled successfully.");
    }

    @PostMapping("/dto")
    public ResponseEntity<?> schedulePostFromDto(@RequestBody ScheduledPostDto dto) {
        ScheduledPost post = new ScheduledPost();
        post.setContent(dto.getMessage());
        post.setImageUrl(dto.getImageUrl());
        post.setScheduledTime(dto.getScheduledTime());
        post.setPlatform("FACEBOOK");
        post.setStatus(PostStatus.SCHEDULED);

        // Lấy thông tin User bằng ID
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));
        post.setUser(user);

        // Tạo thông tin FacebookPage bằng pageId và accessToken
        FacebookPage page = new FacebookPage();
        page.setPageId(dto.getPageId());
        page.setAccessToken(dto.getAccessToken());
        post.setFacebookPage(page);

        // Lưu bài viết
        scheduledPostService.schedulePost(post);

        return ResponseEntity.ok("✅ Đã đặt lịch thành công.");
    }
}

