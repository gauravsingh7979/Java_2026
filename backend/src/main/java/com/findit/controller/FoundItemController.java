package com.findit.controller;

import com.findit.dto.common.ApiResponse;
import com.findit.dto.common.PageResponse;
import com.findit.dto.item.FoundItemRequest;
import com.findit.dto.item.FoundItemResponse;
import com.findit.dto.item.ItemSearchFilter;
import com.findit.security.SecurityUtils;
import com.findit.service.FoundItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/found-items")
@Tag(name = "Found Items", description = "Endpoints for reporting, searching, and managing found items with private details masking")
public class FoundItemController {

    private final FoundItemService foundItemService;

    public FoundItemController(FoundItemService foundItemService) {
        this.foundItemService = foundItemService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Report a found item (JSON)", description = "Saves found item with public and private details, triggering smart matching.")
    public ResponseEntity<ApiResponse<FoundItemResponse>> createFoundItem(@Valid @RequestBody FoundItemRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        FoundItemResponse response = foundItemService.createFoundItem(request, null, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Found item report created. Smart matching triggered."));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Report a found item with images (Multipart)", description = "Saves found item, private clues, and photos, triggering smart matching.")
    public ResponseEntity<ApiResponse<FoundItemResponse>> createFoundItemWithImages(
            @Valid @RequestPart("item") FoundItemRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        Long userId = SecurityUtils.getCurrentUserId();
        FoundItemResponse response = foundItemService.createFoundItem(request, images, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Found item report and images saved. Smart matching triggered."));
    }

    @GetMapping
    @Operation(summary = "Search and filter found items", description = "Public listings. Private details are strictly masked for security.")
    public ResponseEntity<ApiResponse<PageResponse<FoundItemResponse>>> searchFoundItems(@ModelAttribute ItemSearchFilter filter) {
        PageResponse<FoundItemResponse> results = foundItemService.searchFoundItems(filter);
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get found item details", description = "Private details are hidden unless caller is finder or administrator.")
    public ResponseEntity<ApiResponse<FoundItemResponse>> getFoundItemById(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserPrincipal().map(p -> p.getId()).orElse(null);
        FoundItemResponse response = foundItemService.getFoundItemById(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update found item report")
    public ResponseEntity<ApiResponse<FoundItemResponse>> updateFoundItem(
            @PathVariable Long id,
            @Valid @RequestBody FoundItemRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        FoundItemResponse response = foundItemService.updateFoundItem(id, request, userId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Report updated and rematching triggered."));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancel/delete found item report")
    public ResponseEntity<ApiResponse<Void>> deleteFoundItem(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        foundItemService.deleteFoundItem(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Found item report cancelled."));
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload additional images for found item")
    public ResponseEntity<ApiResponse<FoundItemResponse>> uploadImages(
            @PathVariable Long id,
            @RequestParam("images") List<MultipartFile> images) {
        Long userId = SecurityUtils.getCurrentUserId();
        FoundItemResponse response = foundItemService.addImages(id, images, userId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Images uploaded successfully."));
    }
}
