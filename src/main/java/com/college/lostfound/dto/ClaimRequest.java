package com.college.lostfound.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRequest {

    @NotBlank(message = "Explanation is required to support your claim")
    @Size(min = 10, max = 2000, message = "Explanation must be between 10 and 2000 characters")
    private String explanation;

    @Size(max = 1000, message = "Identifying details must not exceed 1000 characters")
    private String identifyingDetails;
}
