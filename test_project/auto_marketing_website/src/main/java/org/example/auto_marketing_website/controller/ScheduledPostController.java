package org.example.auto_marketing_website.controller;


import org.example.auto_marketing_website.dto.ScheduleUpdateDto;
import org.example.auto_marketing_website.dto.ScheduledRequestDto;
import org.example.auto_marketing_website.entity.ScheduledPost;
import org.example.auto_marketing_website.repository.IUserRepository;
import org.example.auto_marketing_website.service.ScheduledPostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
public class ScheduledPostController {
    private final ScheduledPostService scheduledPostService;
    private final IUserRepository userRepository;

    public ScheduledPostController(ScheduledPostService scheduledPostService, IUserRepository userRepository) {
        this.scheduledPostService = scheduledPostService;
        this.userRepository = userRepository;
    }

    // create
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ScheduledRequestDto dto, Authentication authentication) {
        Long userId = getUserId(authentication);
        ScheduledPost post = scheduledPostService.createSchedule(userId, dto.getPageId(), dto.getMessage(), dto.getImageUrl(), dto.getScheduledTime());
        return ResponseEntity.ok(Map.of("message", "Scheduled created", "post", post));
    }

    // update
    @PutMapping
    public ResponseEntity<?> update(@RequestBody ScheduleUpdateDto dto, Authentication authentication) {
        Long userId = getUserId(authentication);
        ScheduledPost post = scheduledPostService.updateSchedule(userId, dto);
        return ResponseEntity.ok(Map.of("message", "Scheduled updated", "post", post));
    }

    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserId(authentication);
        scheduledPostService.deleteSchedule(userId, id);
        return ResponseEntity.ok(Map.of("message", "Scheduled deleted"));
    }

    // list for user
    @GetMapping
    public ResponseEntity<?> list(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(Map.of("data", scheduledPostService.listUserSchedules(userId)));
    }

    private Long getUserId(Authentication authentication) {
        if (authentication == null) throw new RuntimeException("Unauthorized");
        // Try OAuth2 token
        if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.user.DefaultOAuth2User) {
            // fetch facebookId or email then find in DB
            var oauth = (org.springframework.security.oauth2.core.user.DefaultOAuth2User) authentication.getPrincipal();
            String fbId = (String) oauth.getAttribute("id");
            return userRepository.findByFacebookId(fbId).orElseThrow(() -> new RuntimeException("User not found")).getId();
        } else {
            String username = authentication.getName();
            return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found")).getId();
        }
    }
}
