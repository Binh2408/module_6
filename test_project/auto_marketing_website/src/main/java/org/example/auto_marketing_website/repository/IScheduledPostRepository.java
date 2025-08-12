package org.example.auto_marketing_website.repository;

import org.example.auto_marketing_website.entity.PostStatus;
import org.example.auto_marketing_website.entity.ScheduledPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface IScheduledPostRepository extends JpaRepository<ScheduledPost,Long> {
    List<ScheduledPost> findByStatusAndScheduledTimeBefore(PostStatus status, LocalDateTime time);
    List<ScheduledPost> findByUserId(Long userId);
}
