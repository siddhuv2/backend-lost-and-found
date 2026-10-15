package com.college.lostfound.service;

import com.college.lostfound.dto.ItemRequest;
import com.college.lostfound.dto.ItemResponse;
import com.college.lostfound.entity.*;
import com.college.lostfound.exception.BadRequestException;
import com.college.lostfound.exception.ResourceNotFoundException;
import com.college.lostfound.exception.UnauthorizedActionException;
import com.college.lostfound.repository.ClaimRepository;
import com.college.lostfound.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final ClaimRepository claimRepository;

    @Transactional
    public ItemResponse createItem(ItemRequest request, User currentUser) {
        Item item = Item.builder()
                .title(request.getTitle().trim())
                .type(request.getType())
                .category(request.getCategory().trim())
                .description(request.getDescription().trim())
                .campusLocation(request.getCampusLocation().trim())
                .incidentDate(request.getIncidentDate())
                .additionalDetails(request.getAdditionalDetails() != null ? request.getAdditionalDetails().trim() : null)
                .status(ItemStatus.OPEN)
                .reportedBy(currentUser)
                .build();

        Item savedItem = itemRepository.save(item);
        return mapToResponse(savedItem, currentUser);
    }

    @Transactional(readOnly = true)
    public Page<ItemResponse> searchItems(ItemType type, String category, String keyword, int page, int size, User currentUser) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> itemPage = itemRepository.searchItems(type, category, keyword, pageable);
        return itemPage.map(item -> mapToResponse(item, currentUser));
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getRecentItems(User currentUser) {
        return itemRepository.findTop6ByOrderByCreatedAtDesc()
                .stream()
                .map(item -> mapToResponse(item, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ItemResponse getItemById(Long id, User currentUser) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reported item not found with ID: " + id));
        return mapToResponse(item, currentUser);
    }

    @Transactional(readOnly = true)
    public List<ItemResponse> getMyReports(User currentUser) {
        return itemRepository.findByReportedByIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(item -> mapToResponse(item, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemResponse updateItem(Long id, ItemRequest request, User currentUser) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reported item not found with ID: " + id));

        // Ownership & Role Verification: Only reporting user or Admin can update
        boolean isOwner = item.getReportedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedActionException("You are not authorized to edit this report.");
        }

        item.setTitle(request.getTitle().trim());
        item.setType(request.getType());
        item.setCategory(request.getCategory().trim());
        item.setDescription(request.getDescription().trim());
        item.setCampusLocation(request.getCampusLocation().trim());
        item.setIncidentDate(request.getIncidentDate());
        item.setAdditionalDetails(request.getAdditionalDetails() != null ? request.getAdditionalDetails().trim() : null);

        Item updated = itemRepository.save(item);
        return mapToResponse(updated, currentUser);
    }

    @Transactional
    public void deleteItem(Long id, User currentUser) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reported item not found with ID: " + id));

        boolean isOwner = item.getReportedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedActionException("You are not authorized to delete this report.");
        }

        itemRepository.delete(item);
    }

    @Transactional
    public ItemResponse updateItemStatus(Long id, ItemStatus status, User currentUser) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reported item not found with ID: " + id));

        boolean isOwner = item.getReportedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedActionException("You are not authorized to change the status of this report.");
        }

        item.setStatus(status);
        Item updated = itemRepository.save(item);
        return mapToResponse(updated, currentUser);
    }

    public ItemResponse mapToResponse(Item item, User currentUser) {
        boolean isOwner = currentUser != null && item.getReportedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser != null && currentUser.getRole() == Role.ROLE_ADMIN;
        long claimsCount = 0;
        if (item.getType() == ItemType.FOUND) {
            claimsCount = claimRepository.findByItemIdOrderByCreatedAtDesc(item.getId()).size();
        }

        return ItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .type(item.getType())
                .category(item.getCategory())
                .description(item.getDescription())
                .campusLocation(item.getCampusLocation())
                .incidentDate(item.getIncidentDate())
                .additionalDetails(item.getAdditionalDetails())
                .status(item.getStatus())
                .reportedById(item.getReportedBy().getId())
                .reportedByName(item.getReportedBy().getFullName())
                .reportedByDepartment(item.getReportedBy().getDepartment())
                // Only disclose email if current user is owner, admin, or in context of recovery
                .reportedByEmail(isOwner || isAdmin ? item.getReportedBy().getEmail() : null)
                .isOwner(isOwner)
                .claimsCount(claimsCount)
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
