package com.college.lostfound.service;

import com.college.lostfound.dto.ClaimRequest;
import com.college.lostfound.dto.ClaimResponse;
import com.college.lostfound.dto.ClaimStatusUpdateRequest;
import com.college.lostfound.entity.*;
import com.college.lostfound.exception.BadRequestException;
import com.college.lostfound.exception.ResourceNotFoundException;
import com.college.lostfound.exception.UnauthorizedActionException;
import com.college.lostfound.repository.ClaimRepository;
import com.college.lostfound.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public ClaimResponse submitClaim(Long itemId, ClaimRequest request, User claimant) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

        if (item.getType() != ItemType.FOUND) {
            throw new BadRequestException("Claims can only be filed against items reported as FOUND.");
        }

        if (item.getReportedBy().getId().equals(claimant.getId())) {
            throw new BadRequestException("You cannot claim an item you reported yourself.");
        }

        if (item.getStatus() == ItemStatus.CLAIMED || item.getStatus() == ItemStatus.RESOLVED) {
            throw new BadRequestException("This item has already been marked as claimed or resolved.");
        }

        if (claimRepository.existsByItemIdAndClaimantId(itemId, claimant.getId())) {
            throw new BadRequestException("You have already submitted a claim request for this item.");
        }

        Claim claim = Claim.builder()
                .item(item)
                .claimant(claimant)
                .explanation(request.getExplanation().trim())
                .identifyingDetails(request.getIdentifyingDetails() != null ? request.getIdentifyingDetails().trim() : null)
                .status(ClaimStatus.PENDING)
                .build();

        Claim savedClaim = claimRepository.save(claim);
        return mapToResponse(savedClaim);
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getMyClaims(User claimant) {
        return claimRepository.findByClaimantIdOrderByCreatedAtDesc(claimant.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsForMyFoundItems(User finder) {
        return claimRepository.findByItemReportedByIdOrderByCreatedAtDesc(finder.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClaimResponse> getClaimsByItemId(Long itemId, User user) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + itemId));

        boolean isFinder = item.getReportedBy().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isFinder && !isAdmin) {
            throw new UnauthorizedActionException("Only the finder or an administrator can view claims for this item.");
        }

        return claimRepository.findByItemIdOrderByCreatedAtDesc(itemId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClaimResponse updateClaimStatus(Long claimId, ClaimStatusUpdateRequest request, User user) {
        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with ID: " + claimId));

        Item item = claim.getItem();
        boolean isFinder = item.getReportedBy().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isFinder && !isAdmin) {
            throw new UnauthorizedActionException("Only the user who reported this found item can accept or reject claims.");
        }

        claim.setStatus(request.getStatus());
        if (request.getResponseNote() != null) {
            claim.setResponseNote(request.getResponseNote().trim());
        }

        if (request.getStatus() == ClaimStatus.ACCEPTED) {
            item.setStatus(ItemStatus.CLAIMED);
            itemRepository.save(item);
        }

        Claim updatedClaim = claimRepository.save(claim);
        return mapToResponse(updatedClaim);
    }

    private ClaimResponse mapToResponse(Claim claim) {
        return ClaimResponse.builder()
                .id(claim.getId())
                .itemId(claim.getItem().getId())
                .itemTitle(claim.getItem().getTitle())
                .itemCategory(claim.getItem().getCategory())
                .itemCampusLocation(claim.getItem().getCampusLocation())
                .claimantId(claim.getClaimant().getId())
                .claimantName(claim.getClaimant().getFullName())
                .claimantEmail(claim.getClaimant().getEmail())
                .claimantDepartment(claim.getClaimant().getDepartment())
                .claimantCollegeId(claim.getClaimant().getCollegeIdNumber())
                .explanation(claim.getExplanation())
                .identifyingDetails(claim.getIdentifyingDetails())
                .status(claim.getStatus())
                .responseNote(claim.getResponseNote())
                .createdAt(claim.getCreatedAt())
                .updatedAt(claim.getUpdatedAt())
                .build();
    }
}
