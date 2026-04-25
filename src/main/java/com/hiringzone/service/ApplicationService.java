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

    public Map<String, Long> getSeekerStats(User user) {
        return Map.of(
                "totalApplications", repository.countByUserId(user.getId()),
                "interviews", repository.countByUserIdAndStatus(user.getId(), "Shortlisted")
        );
    }

    public Map<String, Object> getEmployerStats(User employer) {
        Integer companyId = employer.getId(); // Simplified
        return Map.of(
                "totalApplications", repository.countByJobCompanyUserId(employer.getId()),
                "activeJobs", jobRepository.countByCompanyId(companyId),
                "shortlisted", repository.countByJobCompanyUserIdAndStatus(employer.getId(), "Shortlisted"),
                "hired", repository.countByJobCompanyUserIdAndStatus(employer.getId(), "Hired")
        );
    }
}
