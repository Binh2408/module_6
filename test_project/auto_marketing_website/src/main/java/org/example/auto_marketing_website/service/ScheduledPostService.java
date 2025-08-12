package org.example.auto_marketing_website.service;

import org.example.auto_marketing_website.dto.ScheduleUpdateDto;
import org.example.auto_marketing_website.entity.*;
import org.example.auto_marketing_website.repository.IFacebookPageRepository;
import org.example.auto_marketing_website.repository.IScheduledPostLogRepository;
import org.example.auto_marketing_website.repository.IScheduledPostRepository;
import org.example.auto_marketing_website.repository.IUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduledPostService {
    private final Logger logger = LoggerFactory.getLogger(ScheduledPostService.class);

    private final IScheduledPostRepository postRepo;
    private final IFacebookPageRepository pageRepo;
    private final IUserRepository userRepo;
    private final IScheduledPostLogRepository logRepo;

    public ScheduledPostService(IScheduledPostRepository postRepo,
                                IFacebookPageRepository pageRepo,
                                IUserRepository userRepo,
                                IScheduledPostLogRepository logRepo) {
        this.postRepo = postRepo;
        this.pageRepo = pageRepo;
        this.userRepo = userRepo;
        this.logRepo = logRepo;
    }

    @Transactional
    public ScheduledPost createSchedule(Long userId, String pageId, String message, String imageUrl, LocalDateTime scheduledTime) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        FacebookPage page = pageRepo.findById(pageId).orElseThrow(() -> new RuntimeException("Page not found"));

        ScheduledPost post = new ScheduledPost();
        post.setUser(user);
        post.setPage(page);
        post.setMessage(message);
        post.setImageUrl(imageUrl);
        post.setScheduledTime(scheduledTime);
        post.setStatus(PostStatus.PENDING);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        return postRepo.save(post);
    }

    @Transactional
    public ScheduledPost updateSchedule(Long userId, ScheduleUpdateDto dto) {
        ScheduledPost post = postRepo.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Schedule not found"));
        if (!post.getUser().getId().equals(userId)) throw new RuntimeException("Not owner");
        if (post.getStatus() != PostStatus.PENDING) throw new RuntimeException("Cannot edit posted/failed items");

        post.setMessage(dto.getMessage());
        post.setImageUrl(dto.getImageUrl());
        post.setScheduledTime(dto.getScheduledTime());
        post.setUpdatedAt(LocalDateTime.now());
        return postRepo.save(post);
    }

    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        ScheduledPost post = postRepo.findById(scheduleId).orElseThrow(() -> new RuntimeException("Schedule not found"));
        if (!post.getUser().getId().equals(userId)) throw new RuntimeException("Not owner");
        if (post.getStatus() != PostStatus.PENDING) throw new RuntimeException("Only pending can be deleted");
        postRepo.delete(post);
    }

    public List<ScheduledPost> listUserSchedules(Long userId) {
        return postRepo.findByUserId(userId);
    }

    public void logAttempt(Long scheduledPostId, boolean success, String result) {
        ScheduledPostLog log = new ScheduledPostLog();
        log.setScheduledPostId(scheduledPostId);
        log.setSuccess(success);
        log.setResult(result);
        log.setAttemptedAt(LocalDateTime.now());
        logRepo.save(log);
    }
}
