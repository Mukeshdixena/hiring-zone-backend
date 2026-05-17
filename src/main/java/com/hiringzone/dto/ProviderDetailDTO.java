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
public class ProviderDetailDTO {
    private Integer id;
    private String companyName;
    private String industry;
    private String description;
    private String website;
    private String location;
    private String email;
    private boolean verified;
    private boolean suspended;
    private long jobCount;
    private long totalApplications;
    private String joinedAt;
    private List<JobSummary> jobs;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobSummary {
        private Integer id;
        private String title;
        private String type;
        private String location;
        private boolean remote;
        private long applicationCount;
        private String postedAt;
        private boolean flagged;
        private boolean expired;
    }
}
