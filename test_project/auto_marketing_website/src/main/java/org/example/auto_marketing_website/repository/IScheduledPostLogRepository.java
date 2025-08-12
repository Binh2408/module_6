package org.example.auto_marketing_website.repository;

import org.example.auto_marketing_website.entity.ScheduledPostLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IScheduledPostLogRepository extends JpaRepository<ScheduledPostLog,Long> {
    List<ScheduledPostLog> findByScheduledPostIdOrderByAttemptedAtDesc(Long scheduledPostId);

}
