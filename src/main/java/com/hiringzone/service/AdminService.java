package com.hiringzone.service;

import com.hiringzone.model.*;
import com.hiringzone.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final AnnouncementRepository announcementRepository;

    public Page<User> getAllUsers(String search, String status, Pageable pageable) {
        // Simplified search/filter
        return userRepository.findAll(pageable);
    }

    public void suspendUser(Integer id, boolean suspended) {
        User user = userRepository.findById(id).orElseThrow();
        user.setSuspended(suspended);
        userRepository.save(user);
    }

    public Page<Company> getAllProviders(String search, String verified, Pageable pageable) {
        return companyRepository.findAll(pageable);
    }

    public void verifyProvider(Integer id) {
        Company company = companyRepository.findById(id).orElseThrow();
        company.setVerified(true);
        companyRepository.save(company);
    }

    public Page<Job> getAllJobs(String search, Boolean flagged, Pageable pageable) {
        return jobRepository.findAllAdmin(search, flagged, pageable);
    }

    public void flagJob(Integer id, boolean flagged) {
        Job job = jobRepository.findById(id).orElseThrow();
        job.setFlagged(flagged);
        jobRepository.save(job);
    }

    public Announcement postAnnouncement(Announcement announcement, User admin) {
        announcement.setCreatedBy(admin);
        return announcementRepository.save(announcement);
    }

    public Map<String, Long> getPlatformStats() {
        return Map.of(
                "totalSeekers", userRepository.count(),
                "totalEmployers", companyRepository.count(),
                "activeJobs", jobRepository.count(),
                "totalApplications", applicationRepository.count()
        );
    }
}
