package com.hiringzone.controller;

import com.hiringzone.model.Application;
import com.hiringzone.model.Job;
import com.hiringzone.model.User;
import com.hiringzone.model.SavedJob;
import com.hiringzone.repository.SavedJobRepository;
import com.hiringzone.service.ApplicationService;
import com.hiringzone.service.FileService;
import com.hiringzone.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SeekerController {

    private final JobService jobService;
    private final ApplicationService applicationService;
    private final FileService fileService;
    private final SavedJobRepository savedJobRepository;

    // Public Job routes
    @GetMapping("/jobs")
    public ResponseEntity<Page<Job>> getJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String experience,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(jobService.getAllJobs(search, location, category, type, experience, PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<Job> getJob(@PathVariable Integer id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    // Seeker specific routes
    @PostMapping("/jobs/{id}/apply")
    public ResponseEntity<Application> apply(
            @PathVariable Integer id,
            @RequestParam(required = false) String coverLetter,
            @RequestParam(required = false) MultipartFile resume,
            @RequestParam(required = false) String guestName,
            @RequestParam(required = false) String guestEmail,
            @AuthenticationPrincipal User user
    ) throws IOException {
        String resumePath = null;
        if (resume != null) {
            resumePath = fileService.saveResume(resume);
        }
        
        Application app = Application.builder()
                .coverLetter(coverLetter)
                .resumePath(resumePath)
                .guestName(guestName)
                .guestEmail(guestEmail)
                .build();
                
        return ResponseEntity.ok(applicationService.apply(app, id, user));
    }

    @GetMapping("/applications")
    public ResponseEntity<Page<Application>> getMyApplications(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(applicationService.getSeekerApplications(user, status, PageRequest.of(page, size, Sort.by("appliedAt").descending())));
    }

    @GetMapping("/applications/stats")
    public ResponseEntity<Map<String, Long>> getStats(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getSeekerStats(user));
    }

    // Saved Jobs
    @GetMapping("/saved-jobs")
    public ResponseEntity<Page<SavedJob>> getSavedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(savedJobRepository.findByUserId(user.getId(), PageRequest.of(page, size)));
    }

    @PostMapping("/saved-jobs/{jobId}")
    public ResponseEntity<SavedJob> saveJob(@PathVariable Integer jobId, @AuthenticationPrincipal User user) {
        if (savedJobRepository.findByUserIdAndJobId(user.getId(), jobId).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Job job = jobService.getJobById(jobId);
        SavedJob savedJob = SavedJob.builder().user(user).job(job).build();
        return ResponseEntity.ok(savedJobRepository.save(savedJob));
    }

    @DeleteMapping("/saved-jobs/{jobId}")
    @Transactional
    public ResponseEntity<Void> unsaveJob(@PathVariable Integer jobId, @AuthenticationPrincipal User user) {
        savedJobRepository.deleteByUserIdAndJobId(user.getId(), jobId);
        return ResponseEntity.noContent().build();
    }
}
