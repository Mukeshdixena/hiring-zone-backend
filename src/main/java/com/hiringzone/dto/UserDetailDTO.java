package com.hiringzone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailDTO {
    private Integer id;
    private String name;
    private String email;
    private String role;
    private boolean suspended;
    private String joinedAt;
    private long applicationCount;
    private List<ApplicationSummary> applications;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApplicationSummary {
        private Integer id;
        private String jobTitle;
        private String company;
        private String status;
        private String appliedAt;
        private String location;
        private String jobType;
    }
}
