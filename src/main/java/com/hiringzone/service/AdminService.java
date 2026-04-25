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

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    public void suspendProvider(Integer id, boolean suspended) {
        Company company = companyRepository.findById(id).orElseThrow();
        company.getUser().setSuspended(suspended);
        userRepository.save(company.getUser());
    }

    public void deleteProvider(Integer id) {
        companyRepository.deleteById(id);
    }

    public void assignRole(String email, Role role) {
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setRole(role);
        userRepository.save(user);
    }

    public void expireJob(Integer id) {
        Job job = jobRepository.findById(id).orElseThrow();
        job.setExpired(true);
        jobRepository.save(job);
    }

    public void deleteJob(Integer id) {
        jobRepository.deleteById(id);
    }

    public Page<Announcement> getAllAnnouncements(Pageable pageable) {
        return announcementRepository.findAll(pageable);
    }

    public java.util.List<com.hiringzone.dto.ActivityDTO> getRecentActivity() {
        java.util.List<com.hiringzone.dto.ActivityDTO> activities = new java.util.ArrayList<>();
        
        // Latest Users
        userRepository.findAll(org.springframework.data.domain.PageRequest.of(0, 3, org.springframework.data.domain.Sort.by("createdAt").descending()))
                .forEach(u -> activities.add(com.hiringzone.dto.ActivityDTO.builder()
                        .type("USER")
                        .message("New seeker registered: " + u.getEmail())
                        .timestamp(u.getCreatedAt())
                        .icon("👤")
                        .iconBg("bg-blue-900/40")
                        .build()));
        
        // Latest Jobs
        jobRepository.findAll(org.springframework.data.domain.PageRequest.of(0, 3, org.springframework.data.domain.Sort.by("createdAt").descending()))
                .forEach(j -> activities.add(com.hiringzone.dto.ActivityDTO.builder()
                        .type("JOB")
                        .message("New job posted: " + j.getTitle())
                        .timestamp(j.getCreatedAt())
                        .icon("💼")
                        .iconBg("bg-violet-900/40")
                        .build()));

        // Latest Applications
        applicationRepository.findAll(org.springframework.data.domain.PageRequest.of(0, 3, org.springframework.data.domain.Sort.by("appliedAt").descending()))
                .forEach(a -> activities.add(com.hiringzone.dto.ActivityDTO.builder()
                        .type("APPLICATION")
                        .message("New application for: " + a.getJob().getTitle())
                        .timestamp(a.getAppliedAt())
                        .icon("📤")
                        .iconBg("bg-adm-900/40")
                        .build()));

        activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return activities.stream().limit(10).collect(java.util.stream.Collectors.toList());
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
