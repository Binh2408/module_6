package org.example.auto_marketing_website.service;

import org.example.auto_marketing_website.entity.PostStatus;
import org.example.auto_marketing_website.entity.ScheduledPost;
import org.example.auto_marketing_website.repository.IScheduledPostRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledPostService {
    private IScheduledPostRepository scheduledPostRepository;
    private FacebookPostService facebookPostService;

    public ScheduledPostService(IScheduledPostRepository scheduledPostRepository, FacebookPostService facebookPostService) {
        this.scheduledPostRepository = scheduledPostRepository;
        this.facebookPostService = facebookPostService;
    }
    public void schedulePost(ScheduledPost post) {
        post.setStatus(PostStatus.SCHEDULED);
        scheduledPostRepository.save(post);
    }


    @Scheduled(fixedRate = 60000)
    public void checkAndPost() {
        List<ScheduledPost> duePosts = scheduledPostRepository
                .findByStatusAndScheduledTimeBefore(PostStatus.SCHEDULED, LocalDateTime.now());

        System.out.println("🕑 Đang kiểm tra bài viết tới hạn: " + duePosts.size() + " bài");

        for (ScheduledPost post : duePosts) {
            try {
                System.out.println("📤 Chuẩn bị đăng bài viết ID: " + post.getId());

                if ("FACEBOOK".equalsIgnoreCase(post.getPlatform())) {
                    facebookPostService.publish(post);
                    System.out.println("✅ Đã gọi facebookPostService.publish cho bài ID: " + post.getId());
                }

                post.setStatus(PostStatus.POSTED);
            } catch (Exception e) {
                post.setStatus(PostStatus.FAILED);
                post.setErrorMessage(e.getMessage());
                System.err.println("❌ Lỗi khi đăng bài ID " + post.getId() + ": " + e.getMessage());
            }

            scheduledPostRepository.save(post);
        }
    }


}
