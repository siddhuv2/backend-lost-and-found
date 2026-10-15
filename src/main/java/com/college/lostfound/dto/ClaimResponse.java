package com.college.lostfound.dto;

import com.college.lostfound.entity.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponse {

    private Long id;
    private Long itemId;
    private String itemTitle;
    private String itemCategory;
    private String itemCampusLocation;

    // Claimant info (sanitized)
    private Long claimantId;
    private String claimantName;
    private String claimantEmail;
    private String claimantDepartment;
    private String claimantCollegeId;

    private String explanation;
    private String identifyingDetails;
    private ClaimStatus status;
    private String responseNote;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
