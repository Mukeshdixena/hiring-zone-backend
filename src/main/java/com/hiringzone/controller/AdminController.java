package com.hiringzone.controller;

import com.hiringzone.dto.*;
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

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService service;

    // ── Users ────────────────────────────────────────────────────────────────

    @GetMapping("/users")
    public ResponseEntity<Page<UserListDTO>> getUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(service.getAllUsers(search, status,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDetailDTO> getUserDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getUserDetail(id));
    }

    @PatchMapping("/users/{id}/suspend")
    public ResponseEntity<Void> suspendUser(@PathVariable Integer id,
                                            @RequestBody Map<String, Boolean> body) {
        service.suspendUser(id, body.get("suspended"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        service.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // ── Providers ────────────────────────────────────────────────────────────

    @GetMapping("/providers")
    public ResponseEntity<Page<ProviderListDTO>> getProviders(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String verified,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(service.getAllProviders(search, verified,
                PageRequest.of(page, size, Sort.by("id").descending())));
    }

    @GetMapping("/providers/{id}")
    public ResponseEntity<ProviderDetailDTO> getProviderDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getProviderDetail(id));
    }

    @PatchMapping("/providers/{id}/verify")
    public ResponseEntity<Void> verifyProvider(@PathVariable Integer id) {
        service.verifyProvider(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/providers/{id}/suspend")
    public ResponseEntity<Void> suspendProvider(@PathVariable Integer id,
                                                @RequestBody Map<String, Boolean> body) {
        service.suspendProvider(id, body.get("suspended"));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/providers/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Integer id) {
        service.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }

    // ── Jobs ─────────────────────────────────────────────────────────────────

    @GetMapping("/jobs")
    public ResponseEntity<Page<Job>> getJobs(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean flagged,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(service.getAllJobs(search, flagged,
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PatchMapping("/jobs/{id}/flag")
    public ResponseEntity<Void> flagJob(@PathVariable Integer id,
                                        @RequestBody Map<String, Boolean> body) {
        service.flagJob(id, body.get("flagged"));
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/jobs/{id}/expire")
    public ResponseEntity<Void> expireJob(@PathVariable Integer id) {
        service.expireJob(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Integer id) {
        service.deleteJob(id);
        return ResponseEntity.noContent().build();
    }

    // ── Announcements ────────────────────────────────────────────────────────

    @PostMapping("/announcements")
    public ResponseEntity<Announcement> postAnnouncement(
            @RequestBody Announcement announcement,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.postAnnouncement(announcement, user));
    }

    @GetMapping("/announcements")
    public ResponseEntity<Page<Announcement>> getAnnouncements(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return ResponseEntity.ok(service.getAllAnnouncements(
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    // ── Roles ────────────────────────────────────────────────────────────────

    @PostMapping("/roles/assign")
    public ResponseEntity<Void> assignRole(@RequestBody Map<String, String> body) {
        service.assignRole(body.get("email"), Role.valueOf(body.get("role")));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/roles/stats")
    public ResponseEntity<Map<String, Object>> getRoleStats() {
        return ResponseEntity.ok(service.getRoleStats());
    }

    // ── Dashboard ────────────────────────────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(service.getPlatformStats());
    }

    @GetMapping("/activity")
    public ResponseEntity<List<ActivityDTO>> getActivity() {
        return ResponseEntity.ok(service.getRecentActivity());
    }
}
