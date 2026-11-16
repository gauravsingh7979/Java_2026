package com.findit.controller;

import com.findit.dto.common.ApiResponse;
import com.findit.dto.common.PageResponse;
import com.findit.dto.recovery.ConfirmHandoverRequest;
import com.findit.dto.recovery.RecoveryResponse;
import com.findit.dto.recovery.ScheduleHandoverRequest;
import com.findit.security.SecurityUtils;
import com.findit.service.RecoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recoveries")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Secure Item Recovery", description = "Endpoints for scheduling and verifying item handover using unique recovery codes")
public class RecoveryController {

    private final RecoveryService recoveryService;

    public RecoveryController(RecoveryService recoveryService) {
        this.recoveryService = recoveryService;
    }

    @GetMapping("/my-recoveries")
    @Operation(summary = "Get recoveries where user is either claimant or finder")
    public ResponseEntity<ApiResponse<PageResponse<RecoveryResponse>>> getMyRecoveries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<RecoveryResponse> response = recoveryService.getUserRecoveries(userId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get recovery transaction by ID")
    public ResponseEntity<ApiResponse<RecoveryResponse>> getRecoveryById(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        RecoveryResponse response = recoveryService.getRecoveryById(id, userId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/{id}/schedule")
    @Operation(summary = "Schedule handover appointment date/location")
    public ResponseEntity<ApiResponse<RecoveryResponse>> scheduleHandover(
            @PathVariable Long id,
            @Valid @RequestBody ScheduleHandoverRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        RecoveryResponse response = recoveryService.scheduleHandover(id, request, userId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Handover scheduled successfully."));
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm handover with recovery code")
    public ResponseEntity<ApiResponse<RecoveryResponse>> confirmHandover(
            @PathVariable Long id,
            @Valid @RequestBody ConfirmHandoverRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        RecoveryResponse response = recoveryService.confirmHandover(id, request, userId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Recovery confirmed! Item marked as RECOVERED."));
    }
}
