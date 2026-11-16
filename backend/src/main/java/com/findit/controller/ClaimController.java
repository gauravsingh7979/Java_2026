package com.findit.controller;

import com.findit.dto.claim.ClaimRequest;
import com.findit.dto.claim.ClaimResponse;
import com.findit.dto.claim.ClaimReviewRequest;
import com.findit.dto.common.ApiResponse;
import com.findit.dto.common.PageResponse;
import com.findit.enums.ClaimStatus;
import com.findit.security.SecurityUtils;
import com.findit.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Claims", description = "Endpoints for submitting and reviewing ownership claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/api/claims")
    @Operation(summary = "Submit an ownership claim for a found item")
    public ResponseEntity<ApiResponse<ClaimResponse>> submitClaim(@Valid @RequestBody ClaimRequest request) {
        Long claimantId = SecurityUtils.getCurrentUserId();
        ClaimResponse response = claimService.submitClaim(request, claimantId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Ownership claim submitted for review."));
    }

    @GetMapping("/api/claims/my-claims")
    @Operation(summary = "Get claims submitted by the current user")
    public ResponseEntity<ApiResponse<PageResponse<ClaimResponse>>> getMyClaims(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long claimantId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<ClaimResponse> response = claimService.getMyClaims(claimantId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/api/claims/{id}")
    @Operation(summary = "Get claim details by ID")
    public ResponseEntity<ApiResponse<ClaimResponse>> getClaimById(@PathVariable Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        ClaimResponse response = claimService.getClaimById(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/api/admin/claims/{id}/review")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Admin: Mark claim UNDER_REVIEW")
    public ResponseEntity<ApiResponse<ClaimResponse>> markUnderReview(@PathVariable Long id) {
        Long adminId = SecurityUtils.getCurrentUserId();
        ClaimResponse response = claimService.markUnderReview(id, adminId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Claim is now under review."));
    }

    @PatchMapping("/api/admin/claims/{id}/approve")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Admin: Approve claim and generate recovery code")
    public ResponseEntity<ApiResponse<ClaimResponse>> approveClaim(
            @PathVariable Long id,
            @Valid @RequestBody ClaimReviewRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        ClaimResponse response = claimService.approveClaim(id, request, adminId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Claim approved and recovery process initiated."));
    }

    @PatchMapping("/api/admin/claims/{id}/reject")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    @Operation(summary = "Admin: Reject claim with reason")
    public ResponseEntity<ApiResponse<ClaimResponse>> rejectClaim(
            @PathVariable Long id,
            @Valid @RequestBody ClaimReviewRequest request) {
        Long adminId = SecurityUtils.getCurrentUserId();
        ClaimResponse response = claimService.rejectClaim(id, request, adminId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Claim rejected."));
    }
}
