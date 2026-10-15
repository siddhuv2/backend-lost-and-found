package com.college.lostfound.dto;

import com.college.lostfound.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private String collegeIdNumber;
    private String department;
    private boolean enabled;
    private LocalDateTime createdAt;
}
