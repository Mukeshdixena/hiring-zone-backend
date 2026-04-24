package com.hiringzone.controller;

import com.hiringzone.model.Application;
import com.hiringzone.model.Job;
import com.hiringzone.model.User;
import com.hiringzone.service.ApplicationService;
import com.hiringzone.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/employer")
@RequiredArgsConstructor
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerController {

    private final JobService jobService;
    private final ApplicationService applicationService;

    @GetMapping("/jobs")
    public ResponseEntity<Page<Job>> getMyJobs(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(jobService.getEmployerJobs(user, search, PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PostMapping("/jobs")
    public ResponseEntity<Job> postJob(@RequestBody Job job, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(jobService.createJob(job, user));
    }

    @PutMapping("/jobs/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Integer id, @RequestBody Job job, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(jobService.updateJob(id, job, user));
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        jobService.deleteJob(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs/{id}/applications")
    public ResponseEntity<Page<Application>> getJobApplications(
            @PathVariable Integer id,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(applicationService.getJobApplications(id, status, user, PageRequest.of(page, size, Sort.by("appliedAt").descending())));
    }

    @PatchMapping("/applications/{appId}/status")
    public ResponseEntity<Application> updateStatus(
            @PathVariable Integer appId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(applicationService.updateStatus(appId, body.get("status"), user));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getEmployerStats(user));
    }
}
