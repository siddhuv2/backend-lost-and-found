package com.college.lostfound.controller;

import com.college.lostfound.dto.AdminStatsResponse;
import com.college.lostfound.dto.UserDto;
import com.college.lostfound.entity.User;
import com.college.lostfound.exception.ResourceNotFoundException;
import com.college.lostfound.repository.UserRepository;
import com.college.lostfound.security.CurrentUser;
import com.college.lostfound.security.UserPrincipal;
import com.college.lostfound.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    private User getUser(UserPrincipal principal) {
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{userId}/toggle-status")
    public ResponseEntity<Map<String, String>> toggleUserStatus(
            @PathVariable Long userId,
            @CurrentUser UserPrincipal principal
    ) {
        User adminUser = getUser(principal);
        adminService.toggleUserStatus(userId, adminUser);
        return ResponseEntity.ok(Map.of("message", "User status updated successfully."));
    }
}
