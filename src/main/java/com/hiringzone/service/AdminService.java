package com.hiringzone.service;

import com.hiringzone.dto.*;
import com.hiringzone.model.*;
import com.hiringzone.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final AnnouncementRepository announcementRepository;

    // ── Users ────────────────────────────────────────────────────────────────

    public Page<UserListDTO> getAllUsers(String search, String status, Pageable pageable) {
        return userRepository.findAllFiltered(search, status, pageable)
                .map(u -> UserListDTO.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .email(u.getEmail())
                        .role(u.getRole().name())
                        .suspended(u.isSuspended())
                        .joinedAt(u.getJoinedAt())
                        .createdAt(u.getCreatedAt())
                        .applicationCount(applicationRepository.countByUserId(u.getId()))
                        .build());
    }

    public UserDetailDTO getUserDetail(Integer id) {
        User user = userRepository.findById(id).orElseThrow();
        List<Application> apps = applicationRepository
                .findByUserId(id, PageRequest.of(0, 50, Sort.by("appliedAt").descending()))
                .getContent();

        List<UserDetailDTO.ApplicationSummary> summaries = apps.stream()
                .map(a -> UserDetailDTO.ApplicationSummary.builder()
                        .id(a.getId())
                        .jobTitle(a.getJob().getTitle())
                        .company(a.getJob().getCompany().getName())
                        .status(a.getStatus())
                        .appliedAt(a.getAppliedAt() != null ? a.getAppliedAt().toLocalDate().toString() : "")
                        .location(a.getJob().getLocation())
                        .jobType(a.getJob().getType())
                        .build())
                .collect(Collectors.toList());

        return UserDetailDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .suspended(user.isSuspended())
                .joinedAt(user.getJoinedAt())
                .applicationCount(applicationRepository.countByUserId(id))
                .applications(summaries)
                .build();
    }

    public void suspendUser(Integer id, boolean suspended) {
        User user = userRepository.findById(id).orElseThrow();
        user.setSuspended(suspended);
        userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    // ── Providers ────────────────────────────────────────────────────────────

    public Page<ProviderListDTO> getAllProviders(String search, String verified, Pageable pageable) {
        Boolean verifiedBool = null;
        if ("verified".equals(verified))   verifiedBool = true;
        if ("unverified".equals(verified)) verifiedBool = false;

        return companyRepository.findAllFiltered(search, verifiedBool, pageable)
                .map(c -> ProviderListDTO.builder()
                        .id(c.getId())
                        .companyName(c.getName())
                        .industry(c.getIndustry())
                        .email(c.getEmail())
                        .verified(c.isVerified())
                        .suspended(c.isSuspended())
                        .jobCount(jobRepository.countByCompanyId(c.getId()))
                        .build());
    }

    public ProviderDetailDTO getProviderDetail(Integer id) {
        Company company = companyRepository.findById(id).orElseThrow();
        List<Job> jobs = jobRepository.findByCompanyId(id);

        List<ProviderDetailDTO.JobSummary> jobSummaries = jobs.stream()
                .map(j -> ProviderDetailDTO.JobSummary.builder()
                        .id(j.getId())
                        .title(j.getTitle())
                        .type(j.getType())
                        .location(j.getLocation())
                        .remote(j.isRemote())
                        .applicationCount(j.getApplicationCount())
                        .postedAt(j.getCreatedAt() != null ? j.getCreatedAt().toLocalDate().toString() : "")
                        .flagged(j.isFlagged())
                        .expired(j.isExpired())
                        .build())
                .collect(Collectors.toList());

        long totalApps = company.getUser() != null
                ? applicationRepository.countByJobCompanyUserId(company.getUser().getId())
                : 0;

        return ProviderDetailDTO.builder()
                .id(company.getId())
                .companyName(company.getName())
                .industry(company.getIndustry())
                .description(company.getDescription())
                .website(company.getWebsite())
                .location(company.getLocation())
                .email(company.getEmail())
                .verified(company.isVerified())
                .suspended(company.isSuspended())
                .jobCount(jobs.size())
                .totalApplications(totalApps)
                .joinedAt(company.getUser() != null ? company.getUser().getJoinedAt() : "")
                .jobs(jobSummaries)
                .build();
    }

    public void verifyProvider(Integer id) {
        Company company = companyRepository.findById(id).orElseThrow();
        company.setVerified(true);
        companyRepository.save(company);
    }

    public void suspendProvider(Integer id, boolean suspended) {
        Company company = companyRepository.findById(id).orElseThrow();
        company.getUser().setSuspended(suspended);
        userRepository.save(company.getUser());
    }

    public void deleteProvider(Integer id) {
        companyRepository.deleteById(id);
    }

    // ── Jobs ─────────────────────────────────────────────────────────────────

    public Page<Job> getAllJobs(String search, Boolean flagged, Pageable pageable) {
        return jobRepository.findAllAdmin(search, flagged, pageable);
    }

    public void flagJob(Integer id, boolean flagged) {
        Job job = jobRepository.findById(id).orElseThrow();
        job.setFlagged(flagged);
        jobRepository.save(job);
    }

    public void expireJob(Integer id) {
        Job job = jobRepository.findById(id).orElseThrow();
        job.setExpired(true);
        jobRepository.save(job);
    }

    public void deleteJob(Integer id) {
        jobRepository.deleteById(id);
    }

    // ── Announcements ────────────────────────────────────────────────────────

    public Announcement postAnnouncement(Announcement announcement, User admin) {
        announcement.setCreatedBy(admin);
        return announcementRepository.save(announcement);
    }

    public Page<Announcement> getAllAnnouncements(Pageable pageable) {
        return announcementRepository.findAll(pageable);
    }

    // ── Roles ────────────────────────────────────────────────────────────────

    public void assignRole(String email, Role role) {
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setRole(role);
        userRepository.save(user);
    }

    public Map<String, Object> getRoleStats() {
        Map<String, Object> map = new HashMap<>();
        map.put("seekers",   userRepository.countByRole(Role.ROLE_SEEKER));
        map.put("employers", userRepository.countByRole(Role.ROLE_EMPLOYER));
        map.put("admins",    userRepository.countByRole(Role.ROLE_ADMIN));
        return map;
    }

    // ── Stats ────────────────────────────────────────────────────────────────

    public Map<String, Object> getPlatformStats() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        long totalSeekers   = userRepository.countByRole(Role.ROLE_SEEKER);
        long seekersBefore  = userRepository.countByRoleAndCreatedAtBefore(Role.ROLE_SEEKER, thirtyDaysAgo);
        int  seekerTrend    = trend(totalSeekers, seekersBefore);

        long totalEmployers  = companyRepository.count();
        long empUsersBefore  = userRepository.countByRoleAndCreatedAtBefore(Role.ROLE_EMPLOYER, thirtyDaysAgo);
        long totalEmpUsers   = userRepository.countByRole(Role.ROLE_EMPLOYER);
        int  employerTrend   = trend(totalEmpUsers, empUsersBefore);

        long totalJobs  = jobRepository.count();
        long jobsBefore = jobRepository.countByCreatedAtBefore(thirtyDaysAgo);
        int  jobTrend   = trend(totalJobs, jobsBefore);

        long totalApps  = applicationRepository.count();
        long appsBefore = applicationRepository.countByAppliedAtBefore(thirtyDaysAgo);
        int  appTrend   = trend(totalApps, appsBefore);

        long hired       = applicationRepository.countByStatus("Hired");
        long successRate = totalApps > 0 ? (hired * 100) / totalApps : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("totalSeekers",      totalSeekers);
        result.put("totalEmployers",    totalEmployers);
        result.put("activeJobs",        totalJobs);
        result.put("totalApplications", totalApps);
        result.put("seekerTrend",       seekerTrend);
        result.put("employerTrend",     employerTrend);
        result.put("jobTrend",          jobTrend);
        result.put("applicationTrend",  appTrend);
        result.put("healthMetrics", Map.of(
                "jobFillRate",        68,
                "applicationSuccess", successRate,
                "employerRetention",  85,
                "platformUptime",     99
        ));
        return result;
    }

    private int trend(long current, long before) {
        if (before <= 0) return 0;
        return (int) Math.round((current - before) * 100.0 / before);
    }

    // ── Activity ─────────────────────────────────────────────────────────────

    public List<ActivityDTO> getRecentActivity() {
        List<ActivityDTO> activities = new ArrayList<>();

        userRepository.findAll(PageRequest.of(0, 3, Sort.by("createdAt").descending()))
                .forEach(u -> activities.add(ActivityDTO.builder()
                        .type("USER")
                        .message("New seeker registered: " + u.getEmail())
                        .timestamp(u.getCreatedAt())
                        .icon("👤")
                        .iconBg("bg-blue-900/40")
                        .build()));

        jobRepository.findAll(PageRequest.of(0, 3, Sort.by("createdAt").descending()))
                .forEach(j -> activities.add(ActivityDTO.builder()
                        .type("JOB")
                        .message("New job posted: " + j.getTitle())
                        .timestamp(j.getCreatedAt())
                        .icon("💼")
                        .iconBg("bg-violet-900/40")
                        .build()));

        applicationRepository.findAll(PageRequest.of(0, 3, Sort.by("appliedAt").descending()))
                .forEach(a -> activities.add(ActivityDTO.builder()
                        .type("APPLICATION")
                        .message("New application for: " + a.getJob().getTitle())
                        .timestamp(a.getAppliedAt())
                        .icon("📤")
                        .iconBg("bg-adm-900/40")
                        .build()));

        activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));
        return activities.stream().limit(10).collect(Collectors.toList());
    }
}
