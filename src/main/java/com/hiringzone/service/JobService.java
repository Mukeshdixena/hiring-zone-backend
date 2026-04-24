package com.hiringzone.service;

import com.hiringzone.model.Company;
import com.hiringzone.model.Job;
import com.hiringzone.model.User;
import com.hiringzone.repository.CompanyRepository;
import com.hiringzone.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository repository;
    private final CompanyRepository companyRepository;

    public Page<Job> getAllJobs(String search, String location, String category, String type, String experience, Pageable pageable) {
        return repository.searchJobs(search, location, category, type, experience, pageable);
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
}
