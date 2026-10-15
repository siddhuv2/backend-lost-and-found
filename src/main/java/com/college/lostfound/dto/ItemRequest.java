package com.college.lostfound.dto;

import com.college.lostfound.entity.ItemType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 150, message = "Item name must be between 2 and 150 characters")
    private String title;

    @NotNull(message = "Item type is required (LOST or FOUND)")
    private ItemType type;

    @NotBlank(message = "Category is required")
    @Size(max = 60, message = "Category name is too long")
    private String category;

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 2000, message = "Description must be between 5 and 2000 characters")
    private String description;

    @NotBlank(message = "Location on campus is required")
    @Size(min = 2, max = 150, message = "Location must be between 2 and 150 characters")
    private String campusLocation;

    @NotNull(message = "Incident date is required")
    private LocalDate incidentDate;

    @Size(max = 1000, message = "Additional details must not exceed 1000 characters")
    private String additionalDetails;
}
