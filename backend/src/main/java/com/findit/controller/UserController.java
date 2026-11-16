package com.findit.controller;

import com.findit.dto.common.ApiResponse;
import com.findit.dto.item.UserReportsSummaryDto;
import com.findit.dto.user.UserProfileResponse;
import com.findit.dto.user.UserUpdateRequest;
import com.findit.security.SecurityUtils;
import com.findit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users", description = "Current authenticated user profile and reports management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        UserProfileResponse profile = userService.getCurrentUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@Valid @RequestBody UserUpdateRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        UserProfileResponse updated = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.ok(updated, "Profile updated successfully"));
    }

    @GetMapping("/me/reports")
    @Operation(summary = "Get all reports submitted by current user", description = "Returns aggregated lost and found reports, match counts, and pending claims.")
    public ResponseEntity<ApiResponse<UserReportsSummaryDto>> getMyReports() {
        Long userId = SecurityUtils.getCurrentUserId();
        UserReportsSummaryDto summary = userService.getUserReportsSummary(userId);
        return ResponseEntity.ok(ApiResponse.ok(summary));
    }
}
