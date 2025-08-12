package org.example.auto_marketing_website.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleUpdateDto {
    private Long id;
    private String message;
    private String imageUrl;
    private LocalDateTime scheduledTime;
}
