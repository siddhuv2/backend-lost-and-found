package com.college.lostfound.service;

import com.college.lostfound.dto.AdminStatsResponse;
import com.college.lostfound.dto.UserDto;
import com.college.lostfound.entity.ItemType;
import com.college.lostfound.entity.User;
import com.college.lostfound.exception.BadRequestException;
import com.college.lostfound.exception.ResourceNotFoundException;
import com.college.lostfound.repository.ClaimRepository;
import com.college.lostfound.repository.ItemRepository;
import com.college.lostfound.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ClaimRepository claimRepository;

    @Transactional(readOnly = true)
    public AdminStatsResponse getStats() {
        return AdminStatsResponse.builder()
                .totalUsers(userRepository.count())
                .totalLostItems(itemRepository.countByType(ItemType.LOST))
                .totalFoundItems(itemRepository.countByType(ItemType.FOUND))
                .totalClaims(claimRepository.count())
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> UserDto.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .collegeIdNumber(user.getCollegeIdNumber())
                        .department(user.getDepartment())
                        .enabled(user.isEnabled())
                        .createdAt(user.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleUserStatus(Long userId, User adminUser) {
        if (userId.equals(adminUser.getId())) {
            throw new BadRequestException("Administrators cannot disable their own account.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }
}
