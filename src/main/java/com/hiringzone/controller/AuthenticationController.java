package com.hiringzone.controller;

import com.hiringzone.dto.AuthenticationRequest;
import com.hiringzone.dto.AuthenticationResponse;
import com.hiringzone.dto.RegisterRequest;
import com.hiringzone.model.Role;
import com.hiringzone.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request, Role.ROLE_SEEKER));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    // Isolated routes for other platforms (optional, but requested for total isolation)
    @PostMapping("/employer/register")
    public ResponseEntity<AuthenticationResponse> registerEmployer(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request, Role.ROLE_EMPLOYER));
    }

    @PostMapping("/employer/login")
    public ResponseEntity<AuthenticationResponse> authenticateEmployer(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AuthenticationResponse> authenticateAdmin(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }
}
