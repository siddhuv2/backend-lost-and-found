package com.college.lostfound.controller;

import com.college.lostfound.dto.ClaimRequest;
import com.college.lostfound.dto.ClaimResponse;
import com.college.lostfound.dto.ClaimStatusUpdateRequest;
import com.college.lostfound.entity.User;
import com.college.lostfound.exception.ResourceNotFoundException;
import com.college.lostfound.repository.UserRepository;
import com.college.lostfound.security.CurrentUser;
import com.college.lostfound.security.UserPrincipal;
import com.college.lostfound.service.ClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;
    private final UserRepository userRepository;

    private User getUser(UserPrincipal principal) {
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @PostMapping("/items/{itemId}")
    public ResponseEntity<ClaimResponse> submitClaim(
            @PathVariable Long itemId,
            @Valid @RequestBody ClaimRequest request,
            @CurrentUser UserPrincipal principal
    ) {
        User currentUser = getUser(principal);
        ClaimResponse response = claimService.submitClaim(itemId, request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<ClaimResponse>> getMyClaims(@CurrentUser UserPrincipal principal) {
        User currentUser = getUser(principal);
        return ResponseEntity.ok(claimService.getMyClaims(currentUser));
    }

    @GetMapping("/received")
    public ResponseEntity<List<ClaimResponse>> getClaimsForMyFoundItems(@CurrentUser UserPrincipal principal) {
        User currentUser = getUser(principal);
        return ResponseEntity.ok(claimService.getClaimsForMyFoundItems(currentUser));
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<List<ClaimResponse>> getClaimsByItem(
            @PathVariable Long itemId,
            @CurrentUser UserPrincipal principal
    ) {
        User currentUser = getUser(principal);
        return ResponseEntity.ok(claimService.getClaimsByItemId(itemId, currentUser));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ClaimResponse> updateClaimStatus(
            @PathVariable Long id,
            @Valid @RequestBody ClaimStatusUpdateRequest request,
            @CurrentUser UserPrincipal principal
    ) {
        User currentUser = getUser(principal);
        ClaimResponse response = claimService.updateClaimStatus(id, request, currentUser);
        return ResponseEntity.ok(response);
    }
}
