package com.hiringzone.service;

import com.hiringzone.model.Company;
import com.hiringzone.model.Job;
import com.hiringzone.model.User;
import com.hiringzone.repository.CompanyRepository;
import com.hiringzone.repository.JobRepository;
import com.hiringzone.repository.JobSpecification;
import com.hiringzone.repository.UserRepository;
import com.hiringzone.repository.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository repository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    public Page<Job> getAllJobs(String keyword, String location, String category,
                               List<String> types, List<String> expLevels,
                               BigDecimal minSalary, Pageable pageable) {
        Specification<Job> spec = Specification.where(JobSpecification.active())
                .and(JobSpecification.keyword(keyword))
                .and(JobSpecification.location(location))
                .and(JobSpecification.category(category))
                .and(JobSpecification.types(types))
                .and(JobSpecification.experienceLevels(expLevels))
                .and(JobSpecification.minSalary(minSalary));
        return repository.findAll(spec, pageable);
    }

    public Job getJobById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public Job createJob(Job job, User employer) {
        Company company = companyRepository.findByUserId(employer.getId())
                .orElseThrow(() -> new RuntimeException("Company profile not found"));
        job.setCompany(company);
        return repository.save(job);
    }

    public Job updateJob(Integer id, Job jobDetails, User employer) {
        Job job = repository.findById(id).orElseThrow();
        if (!job.getCompany().getUser().getId().equals(employer.getId())) {
            throw new RuntimeException("Unauthorized to update this job");
        }
        job.setTitle(jobDetails.getTitle());
        job.setDescription(jobDetails.getDescription());
        job.setRequirements(jobDetails.getRequirements());
        job.setBenefits(jobDetails.getBenefits());
        job.setLocation(jobDetails.getLocation());
        job.setType(jobDetails.getType());
        job.setExperienceLevel(jobDetails.getExperienceLevel());
        job.setCategory(jobDetails.getCategory());
        job.setSalaryMin(jobDetails.getSalaryMin());
        job.setSalaryMax(jobDetails.getSalaryMax());
        job.setRemote(jobDetails.isRemote());
        job.setDeadline(jobDetails.getDeadline());
        return repository.save(job);
    }

    public void deleteJob(Integer id, User employer) {
        Job job = repository.findById(id).orElseThrow();
        if (!job.getCompany().getUser().getId().equals(employer.getId())) {
            throw new RuntimeException("Unauthorized to delete this job");
        }
        repository.delete(job);
    }

    public Page<Job> getEmployerJobs(User employer, String search, Pageable pageable) {
        Company company = companyRepository.findByUserId(employer.getId()).orElseThrow();
        return repository.findByCompanyIdWithSearch(company.getId(), search, pageable);
    }

    private java.util.Map<String, Long> buildCategoryCounts() {
        java.util.Map<String, Long> counts = new java.util.LinkedHashMap<>();
        for (String cat : com.hiringzone.controller.MetaController.CATEGORIES) {
            counts.put(cat, repository.countByCategory(cat));
        }
        return counts;
    }

    public java.util.Map<String, Object> getPublicStats() {
        long totalApplications = applicationRepository.count();
        long hired = applicationRepository.countByStatus("Hired");
        long placementRate = totalApplications > 0 ? (hired * 100) / totalApplications : 0;

        return java.util.Map.of(
                "activeJobs", repository.count(),
                "companiesHiring", companyRepository.count(),
                "jobSeekers", userRepository.countByRole(com.hiringzone.model.Role.ROLE_SEEKER),
                "placementRate", placementRate,
                "categories", buildCategoryCounts()
        );
    }
}
