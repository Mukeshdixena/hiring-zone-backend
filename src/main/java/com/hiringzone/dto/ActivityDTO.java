package com.hiringzone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDTO {
    private Integer id;
    private String type; // USER, JOB, APPLICATION
    private String message;
    private LocalDateTime timestamp;
    private String icon;
    private String iconBg;
}
