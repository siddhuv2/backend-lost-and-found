package com.college.lostfound.dto;

import com.college.lostfound.entity.ItemStatus;
import com.college.lostfound.entity.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {

    private Long id;
    private String title;
    private ItemType type;
    private String category;
    private String description;
    private String campusLocation;
    private LocalDate incidentDate;
    private String additionalDetails;
    private ItemStatus status;

    // Reporting user details (sanitized - no passwords/credentials)
    private Long reportedById;
    private String reportedByName;
    private String reportedByDepartment;
    private String reportedByEmail; // Shown to authenticated user or finder context

    private boolean isOwner; // Computed for current authenticated user
    private long claimsCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
