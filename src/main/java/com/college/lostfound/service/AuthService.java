package com.college.lostfound.service;

import com.college.lostfound.dto.AuthResponse;
import com.college.lostfound.dto.LoginRequest;
import com.college.lostfound.dto.RegisterRequest;
import com.college.lostfound.dto.UserDto;
import com.college.lostfound.entity.Role;
import com.college.lostfound.entity.User;
import com.college.lostfound.exception.BadRequestException;
import com.college.lostfound.repository.UserRepository;
import com.college.lostfound.security.JwtTokenProvider;
import com.college.lostfound.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Password and confirm password do not match.");
        }

        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("An account with this email address already exists.");
        }

        if (userRepository.existsByCollegeIdNumber(request.getCollegeIdNumber().trim())) {
            throw new BadRequestException("An account with this Student/Staff ID already exists.");
        }

        // Enforce: Public registration cannot select ADMIN role
        Role userRole = request.getRole();
        if (userRole == Role.ROLE_ADMIN) {
            throw new BadRequestException("Administrator registration is not permitted via public registration.");
        }
        if (userRole == null) {
            userRole = Role.ROLE_STUDENT;
        }

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .collegeIdNumber(request.getCollegeIdNumber().trim().toUpperCase())
                .department(request.getDepartment().trim())
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        // Authenticate immediately to provide seamless login
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(savedUser.getEmail(), request.getPassword())
        );

        String jwt = tokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(savedUser.getId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .collegeIdNumber(savedUser.getCollegeIdNumber())
                .department(savedUser.getDepartment())
                .message("Account registered successfully!")
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim().toLowerCase(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new BadRequestException("User record not found"));

        return AuthResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .collegeIdNumber(user.getCollegeIdNumber())
                .department(user.getDepartment())
                .message("Login successful")
                .build();
    }

    @Transactional(readOnly = true)
    public UserDto getCurrentUserDto(User currentUser) {
        return UserDto.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .role(currentUser.getRole())
                .collegeIdNumber(currentUser.getCollegeIdNumber())
                .department(currentUser.getDepartment())
                .enabled(currentUser.isEnabled())
                .createdAt(currentUser.getCreatedAt())
                .build();
    }
}
