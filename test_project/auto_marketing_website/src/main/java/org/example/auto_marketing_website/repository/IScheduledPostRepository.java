package org.example.auto_marketing_website.repository;

import org.example.auto_marketing_website.entity.PostStatus;
import org.example.auto_marketing_website.entity.ScheduledPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IScheduledPostRepository extends JpaRepository<ScheduledPost,Long> {
    List<ScheduledPost> findByStatusAndScheduledTimeBefore(PostStatus status, LocalDateTime time);

}
