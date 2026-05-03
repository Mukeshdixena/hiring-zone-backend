package com.hiringzone.service;

import com.hiringzone.model.Application;
import com.hiringzone.model.Job;
import com.hiringzone.model.User;
import com.hiringzone.repository.ApplicationRepository;
import com.hiringzone.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository repository;
    private final JobRepository jobRepository;

    public Application apply(Application application, Integer jobId, User user) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        application.setJob(job);
        application.setUser(user);
        return repository.save(application);
    }

    public boolean hasUserApplied(Integer userId, Integer jobId) {
        return repository.findByUserIdAndJobId(userId, jobId).isPresent();
    }

    public Page<Application> getSeekerApplications(User user, String status, Pageable pageable) {
        if (status != null && !status.isEmpty()) {
            return repository.findByUserIdAndStatus(user.getId(), status, pageable);
        }
        return repository.findByUserId(user.getId(), pageable);
    }

    public Page<Application> getJobApplications(Integer jobId, String status, User employer, Pageable pageable) {
        Job job = jobRepository.findById(jobId).orElseThrow();
        if (!job.getCompany().getUser().getId().equals(employer.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        if (status != null && !status.isEmpty()) {
            return repository.findByJobIdAndStatus(jobId, status, pageable);
        }
        return repository.findByJobId(jobId, pageable);
    }

    public Page<Application> getRecentApplications(User employer, Pageable pageable) {
        return repository.findByJobCompanyUserId(employer.getId(), pageable);
    }

    public Application updateStatus(Integer appId, String status, User employer) {
        Application app = repository.findById(appId).orElseThrow();
        if (!app.getJob().getCompany().getUser().getId().equals(employer.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        app.setStatus(status);
        return repository.save(app);
    }

    private final com.hiringzone.repository.SeekerProfileRepository profileRepository;

    public Map<String, Object> getSeekerStats(User user) {
        int profileCompletion = 0;
        int profileViews = 0;
        
        var profileOpt = profileRepository.findByUserId(user.getId());
        if (profileOpt.isPresent()) {
            profileCompletion = profileOpt.get().calculateCompletion();
            profileViews = profileOpt.get().getProfileViews() != null ? profileOpt.get().getProfileViews() : 0;
        }

        return Map.of(
                "totalApplications", repository.countByUserId(user.getId()),
                "interviews", repository.countByUserIdAndStatus(user.getId(), "Shortlisted"),
                "profileCompletion", profileCompletion,
                "profileViews", profileViews
        );
    }

    public Map<String, Object> getEmployerStats(User employer) {
        java.time.LocalDateTime startOfMonth = java.time.LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        long totalApps = repository.countByJobCompanyUserId(employer.getId());
        long activeJobs = jobRepository.countByCompanyUserIdAndCreatedAtAfter(employer.getId(), java.time.LocalDateTime.of(2000, 1, 1, 0, 0)); // All jobs for now, or filter by expired=false
        long shortlisted = repository.countByJobCompanyUserIdAndStatus(employer.getId(), "Shortlisted");
        long hired = repository.countByJobCompanyUserIdAndStatus(employer.getId(), "Hired");

        return Map.of(
                "totalApplications", totalApps,
                "totalApplicationsTrend", repository.countByJobCompanyUserIdAndAppliedAtAfter(employer.getId(), startOfMonth),
                "activeJobs", activeJobs,
                "activeJobsTrend", jobRepository.countByCompanyUserIdAndCreatedAtAfter(employer.getId(), startOfMonth),
                "shortlisted", shortlisted,
                "shortlistedTrend", repository.countByJobCompanyUserIdAndStatusAndAppliedAtAfter(employer.getId(), "Shortlisted", startOfMonth),
                "hired", hired,
                "hiredTrend", repository.countByJobCompanyUserIdAndStatusAndAppliedAtAfter(employer.getId(), "Hired", startOfMonth)
        );
    }
}
