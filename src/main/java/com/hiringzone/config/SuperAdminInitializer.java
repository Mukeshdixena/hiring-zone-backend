package com.hiringzone.config;

import com.hiringzone.model.Role;
import com.hiringzone.model.User;
import com.hiringzone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${application.super-admin.email}")
    private String adminEmail;
    @Value("${application.super-admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            User admin = User.builder()
                    .name("Super Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ROLE_ADMIN)
                    .suspended(false)
                    .build();
            userRepository.save(admin);
            System.out.println("Super Admin seeded: " + adminEmail);
        }
    }
}
