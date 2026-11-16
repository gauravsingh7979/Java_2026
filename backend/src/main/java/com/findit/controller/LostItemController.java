package com.findit.controller;

import com.findit.dto.common.ApiResponse;
import com.findit.dto.common.PageResponse;
import com.findit.dto.item.ItemSearchFilter;
import com.findit.dto.item.LostItemRequest;
import com.findit.dto.item.LostItemResponse;
import com.findit.security.SecurityUtils;
import com.findit.service.LostItemService;
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
@RequestMapping("/api/lost-items")
@Tag(name = "Lost Items", description = "Endpoints for reporting, searching, and managing lost items")
public class LostItemController {

    private final LostItemService lostItemService;

    public LostItemController(LostItemService lostItemService) {
        this.lostItemService = lostItemService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Report a lost item (JSON)", description = "Saves lost item and automatically triggers smart matching engine.")
    public ResponseEntity<ApiResponse<LostItemResponse>> createLostItem(@Valid @RequestBody LostItemRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        LostItemResponse response = lostItemService.createLostItem(request, null, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Lost item report created successfully. Smart matching triggered."));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Report a lost item with images (Multipart)", description = "Saves lost item and uploaded photos, triggering smart matching.")
    public ResponseEntity<ApiResponse<LostItemResponse>> createLostItemWithImages(
            @Valid @RequestPart("item") LostItemRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        Long userId = SecurityUtils.getCurrentUserId();
        LostItemResponse response = lostItemService.createLostItem(request, images, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Lost item report and images saved. Smart matching triggered."));
    }

    @GetMapping
    @Operation(summary = "Search and filter lost items", description = "Database-side dynamic filtering with pagination and sorting.")
    public ResponseEntity<ApiResponse<PageResponse<LostItemResponse>>> searchLostItems(@ModelAttribute ItemSearchFilter filter) {
        PageResponse<LostItemResponse> results = lostItemService.searchLostItems(filter);
        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lost item details by ID")
    public ResponseEntity<ApiResponse<LostItemResponse>> getLostItemById(@PathVariable Long id) {
        LostItemResponse response = lostItemService.getLostItemById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update lost item report")
    public ResponseEntity<ApiResponse<LostItemResponse>> updateLostItem(
            @PathVariable Long id,
            @Valid @RequestBody LostItemRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        LostItemResponse response = lostItemService.updateLostItem(id, request, userId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Report updated and rematching triggered."));
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancel/delete lost item report")
    public ResponseEntity<ApiResponse<Void>> deleteLostItem(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        lostItemService.deleteLostItem(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(null, "Lost item report cancelled successfully."));
    }

    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Upload additional images for lost item")
    public ResponseEntity<ApiResponse<LostItemResponse>> uploadImages(
            @PathVariable Long id,
            @RequestParam("images") List<MultipartFile> images) {
        Long userId = SecurityUtils.getCurrentUserId();
        LostItemResponse response = lostItemService.addImages(id, images, userId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Images uploaded successfully."));
    }
}
