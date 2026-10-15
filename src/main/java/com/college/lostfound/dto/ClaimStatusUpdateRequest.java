package com.college.lostfound.dto;

import com.college.lostfound.entity.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimStatusUpdateRequest {

    @NotNull(message = "Claim status is required (ACCEPTED or REJECTED)")
    private ClaimStatus status;

    @Size(max = 1000, message = "Response note must not exceed 1000 characters")
    private String responseNote;
}
