package com.hiringzone.controller;

import com.hiringzone.model.*;
import com.hiringzone.service.AdminService;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService service;

    @GetMapping("/users")
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(service.getAllUsers(search, status, PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PatchMapping("/users/{id}/suspend")
    public ResponseEntity<Void> suspendUser(@PathVariable Integer id, @RequestBody Map<String, Boolean> body) {
        service.suspendUser(id, body.get("suspended"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/providers")
    public ResponseEntity<Page<Company>> getProviders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String verified,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(service.getAllProviders(search, verified, PageRequest.of(page, size)));
    }

    @PatchMapping("/providers/{id}/verify")
    public ResponseEntity<Void> verifyProvider(@PathVariable Integer id) {
        service.verifyProvider(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/jobs")
    public ResponseEntity<Page<Job>> getJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean flagged,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(service.getAllJobs(search, flagged, PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PatchMapping("/jobs/{id}/flag")
    public ResponseEntity<Void> flagJob(@PathVariable Integer id, @RequestBody Map<String, Boolean> body) {
        service.flagJob(id, body.get("flagged"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/announcements")
    public ResponseEntity<Announcement> postAnnouncement(@RequestBody Announcement announcement, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.postAnnouncement(announcement, user));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(service.getPlatformStats());
    }
}
