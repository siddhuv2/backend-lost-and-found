package com.college.lostfound.controller;

import com.college.lostfound.dto.ItemRequest;
import com.college.lostfound.dto.ItemResponse;
import com.college.lostfound.entity.ItemStatus;
import com.college.lostfound.entity.ItemType;
import com.college.lostfound.entity.User;
import com.college.lostfound.exception.ResourceNotFoundException;
import com.college.lostfound.repository.UserRepository;
import com.college.lostfound.security.CurrentUser;
import com.college.lostfound.security.UserPrincipal;
import com.college.lostfound.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final UserRepository userRepository;

    private User getUser(UserPrincipal principal) {
        if (principal == null) return null;
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @GetMapping
    public ResponseEntity<Page<ItemResponse>> searchItems(
            @RequestParam(required = false) ItemType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @CurrentUser UserPrincipal principal
    ) {
        User currentUser = getUser(principal);
        Page<ItemResponse> items = itemService.searchItems(type, category, keyword, page, size, currentUser);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ItemResponse>> getRecentItems(@CurrentUser UserPrincipal principal) {
        User currentUser = getUser(principal);
        return ResponseEntity.ok(itemService.getRecentItems(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id, @CurrentUser UserPrincipal principal) {
        User currentUser = getUser(principal);
        return ResponseEntity.ok(itemService.getItemById(id, currentUser));
    }

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(
            @Valid @RequestBody ItemRequest request,
            @CurrentUser UserPrincipal principal
    ) {
        User currentUser = getUser(principal);
        ItemResponse response = itemService.createItem(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponse> updateItem(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequest request,
            @CurrentUser UserPrincipal principal
    ) {
        User currentUser = getUser(principal);
        ItemResponse response = itemService.updateItem(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ItemResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @CurrentUser UserPrincipal principal
    ) {
        User currentUser = getUser(principal);
        ItemStatus status = ItemStatus.valueOf(body.get("status"));
        ItemResponse response = itemService.updateItemStatus(id, status, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteItem(
            @PathVariable Long id,
            @CurrentUser UserPrincipal principal
    ) {
        User currentUser = getUser(principal);
        itemService.deleteItem(id, currentUser);
        return ResponseEntity.ok(Map.of("message", "Report deleted successfully."));
    }

    @GetMapping("/my-reports")
    public ResponseEntity<List<ItemResponse>> getMyReports(@CurrentUser UserPrincipal principal) {
        User currentUser = getUser(principal);
        return ResponseEntity.ok(itemService.getMyReports(currentUser));
    }
}
