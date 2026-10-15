package com.college.lostfound.config;

import com.college.lostfound.entity.Role;
import com.college.lostfound.entity.User;
import com.college.lostfound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.initial-email}")
    private String adminEmail;

    @Value("${app.admin.initial-password}")
    private String adminPassword;

    @Value("${app.admin.initial-name}")
    private String adminName;

    @Value("${app.admin.initial-id}")
    private String adminId;

    @Value("${app.admin.initial-department}")
    private String adminDepartment;

    @Bean
    public CommandLineRunner initializeAdminUser() {
        return args -> {
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .fullName(adminName)
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(Role.ROLE_ADMIN)
                        .collegeIdNumber(adminId)
                        .department(adminDepartment)
                        .enabled(true)
                        .build();

                userRepository.save(admin);
                log.info("Initialized default administrator account: {}", adminEmail);
            } else {
                log.info("Administrator account already exists: {}", adminEmail);
            }
        };
    }
}
