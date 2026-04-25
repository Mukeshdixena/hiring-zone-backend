package com.hiringzone.controller;

import com.hiringzone.model.Application;
import com.hiringzone.model.Job;
import com.hiringzone.model.User;
import com.hiringzone.model.SavedJob;
import com.hiringzone.repository.SavedJobRepository;
import com.hiringzone.service.ApplicationService;
import com.hiringzone.service.FileService;
import com.hiringzone.service.JobService;
import com.hiringzone.model.SeekerProfile;
import com.hiringzone.service.ProfileService;
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
    private final ProfileService profileService;

    // Profile routes
    @GetMapping("/profile")
    public ResponseEntity<SeekerProfile> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.getProfile(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<SeekerProfile> updateProfile(@RequestBody SeekerProfile profile, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.updateProfile(profile, user));
    }

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
    public ResponseEntity<Application> applyAuth(@PathVariable Integer id, @RequestBody Map<String, String> body, @AuthenticationPrincipal User user) {
        Application app = Application.builder().coverLetter(body.get("coverLetter")).build();
        return ResponseEntity.ok(applicationService.apply(app, id, user));
    }

    @PostMapping("/jobs/{id}/apply/guest")
    public ResponseEntity<Application> applyGuest(
            @PathVariable Integer id,
            @RequestParam(value = "resume", required = false) MultipartFile resume,
            @RequestParam(value = "name", required = false) String guestName,
            @RequestParam(value = "email", required = false) String guestEmail
    ) throws IOException {
        String resumePath = null;
        if (resume != null) resumePath = fileService.saveResume(resume);
        Application app = Application.builder().resumePath(resumePath).guestName(guestName).guestEmail(guestEmail).build();
        return ResponseEntity.ok(applicationService.apply(app, id, null));
    }

    @GetMapping("/jobs/{id}/applied-status")
    public ResponseEntity<Map<String, Boolean>> checkApplied(@PathVariable Integer id, @AuthenticationPrincipal User user) {
        boolean applied = applicationService.hasUserApplied(user.getId(), id);
        return ResponseEntity.ok(Map.of("applied", applied));
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

    @GetMapping("/saved-jobs")
    public ResponseEntity<Page<SavedJob>> getSavedJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(savedJobRepository.findByUserId(user.getId(), PageRequest.of(page, size)));
    }

    @PostMapping("/jobs/{jobId}/save")
    public ResponseEntity<SavedJob> saveJob(@PathVariable Integer jobId, @AuthenticationPrincipal User user) {
        if (savedJobRepository.findByUserIdAndJobId(user.getId(), jobId).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Job job = jobService.getJobById(jobId);
        SavedJob savedJob = SavedJob.builder().user(user).job(job).build();
        return ResponseEntity.ok(savedJobRepository.save(savedJob));
    }

    @DeleteMapping("/jobs/{jobId}/save")
    @Transactional
    public ResponseEntity<Void> unsaveJob(@PathVariable Integer jobId, @AuthenticationPrincipal User user) {
        savedJobRepository.deleteByUserIdAndJobId(user.getId(), jobId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/jobs/{jobId}/saved-status")
    public ResponseEntity<Map<String, Boolean>> checkSaved(@PathVariable Integer jobId, @AuthenticationPrincipal User user) {
        boolean saved = savedJobRepository.findByUserIdAndJobId(user.getId(), jobId).isPresent();
        return ResponseEntity.ok(Map.of("saved", saved));
    }
    @GetMapping("/stats/public")
    public ResponseEntity<Map<String, Object>> getPublicStats() {
        return ResponseEntity.ok(jobService.getPublicStats());
    }
}
