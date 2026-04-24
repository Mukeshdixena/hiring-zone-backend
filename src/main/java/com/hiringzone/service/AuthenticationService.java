package com.hiringzone.service;

import com.hiringzone.dto.*;
import com.hiringzone.model.Company;
import com.hiringzone.model.Role;
import com.hiringzone.model.User;
import com.hiringzone.repository.CompanyRepository;
import com.hiringzone.repository.UserRepository;
import com.hiringzone.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request, Role role) {
        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();
        var savedUser = repository.save(user);
        
        String companyName = null;
        if (role == Role.ROLE_EMPLOYER) {
            var company = Company.builder()
                    .user(savedUser)
                    .name(request.getCompanyName())
                    .industry(request.getIndustry())
                    .build();
            companyRepository.save(company);
            companyName = company.getName();
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(UserResponse.builder()
                        .id(savedUser.getId())
                        .email(savedUser.getEmail())
                        .name(savedUser.getName())
                        .role(savedUser.getRole())
                        .companyName(companyName)
                        .build())
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        
        String companyName = null;
        if (user.getRole() == Role.ROLE_EMPLOYER) {
            companyName = companyRepository.findByUserId(user.getId())
                    .map(Company::getName)
                    .orElse(null);
        }

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .user(UserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .role(user.getRole())
                        .companyName(companyName)
                        .build())
                .build();
    }
}
