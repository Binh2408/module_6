package org.example.auto_marketing_website.scheduler;

import org.example.auto_marketing_website.entity.PostStatus;
import org.example.auto_marketing_website.entity.ScheduledPost;
import org.example.auto_marketing_website.repository.IScheduledPostRepository;
import org.example.auto_marketing_website.service.FacebookApiService;
import org.example.auto_marketing_website.service.ScheduledPostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PostScheduler {
    private final Logger logger = LoggerFactory.getLogger(PostScheduler.class);

    private final IScheduledPostRepository postRepository;
    private final FacebookApiService facebookApiService;
    private final ScheduledPostService scheduledPostService;

    public PostScheduler(IScheduledPostRepository postRepository,
                         FacebookApiService facebookApiService,
                         ScheduledPostService scheduledPostService) {
        this.postRepository = postRepository;
        this.facebookApiService = facebookApiService;
        this.scheduledPostService = scheduledPostService;
    }

    @Scheduled(fixedRate = 60000) // every minute
    @Transactional
    public void run() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledPost> due = postRepository.findByStatusAndScheduledTimeBefore(PostStatus.PENDING, now);
        for (ScheduledPost p : due) {
            try {
                String pageAccessToken = p.getPage().getPageAccessToken();
                String pageId = p.getPage().getPageId();
                String postId;
                if (p.getImageUrl() != null && !p.getImageUrl().isEmpty()) {
                    postId = facebookApiService.postImageToPage(pageId, pageAccessToken, p.getMessage(), p.getImageUrl());
                } else {
                    postId = facebookApiService.postTextToPage(pageId, pageAccessToken, p.getMessage());
                }
                p.setStatus(PostStatus.POSTED);
                p.setUpdatedAt(LocalDateTime.now());
                postRepository.save(p);

                scheduledPostService.logAttempt(p.getId(), true, "Posted id=" + postId);
                logger.info("Posted scheduled post id={} to page={} fbPostId={}", p.getId(), pageId, postId);
            } catch (Exception ex) {
                logger.error("Failed to post scheduled id={} error={}", p.getId(), ex.getMessage(), ex);
                p.setStatus(PostStatus.FAILED);
                p.setFailedReason(ex.getMessage());
                p.setUpdatedAt(LocalDateTime.now());
                postRepository.save(p);

                scheduledPostService.logAttempt(p.getId(), false, ex.getMessage());
            }
        }
    }
}
