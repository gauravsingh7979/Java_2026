package com.findit.controller;

import com.findit.dto.admin.AdminDashboardStatsDto;
import com.findit.dto.admin.AuditLogResponse;
import com.findit.dto.claim.ClaimResponse;
import com.findit.dto.common.ApiResponse;
import com.findit.dto.common.PageResponse;
import com.findit.dto.item.FoundItemResponse;
import com.findit.dto.item.LostItemResponse;
import com.findit.dto.user.UserSummaryDto;
import com.findit.enums.ClaimStatus;
import com.findit.service.AdminService;
import com.findit.service.ClaimService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
@Tag(name = "Admin Operations", description = "Administration, moderation, claim approval, and system metrics")
public class AdminController {

    private final AdminService adminService;
    private final ClaimService claimService;

    public AdminController(AdminService adminService, ClaimService claimService) {
        this.adminService = adminService;
        this.claimService = claimService;
    }

    @GetMapping({"/dashboard", "/stats"})
    @Operation(summary = "Get platform-wide dashboard metrics and stats")
    public ResponseEntity<ApiResponse<AdminDashboardStatsDto>> getDashboardStats() {
        AdminDashboardStatsDto stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/reports/lost")
    @Operation(summary = "Get all lost reports for moderation")
    public ResponseEntity<ApiResponse<List<LostItemResponse>>> getAllLostReports() {
        List<LostItemResponse> reports = adminService.getAllLostReportsForModeration();
        return ResponseEntity.ok(ApiResponse.ok(reports));
    }

    @GetMapping("/reports/found")
    @Operation(summary = "Get all found reports for moderation (unmasked private details)")
    public ResponseEntity<ApiResponse<List<FoundItemResponse>>> getAllFoundReports() {
        List<FoundItemResponse> reports = adminService.getAllFoundReportsForModeration();
        return ResponseEntity.ok(ApiResponse.ok(reports));
    }

    @GetMapping("/claims")
    @Operation(summary = "Get claims for administrative review (filter by status)")
    public ResponseEntity<ApiResponse<PageResponse<ClaimResponse>>> getClaims(
            @RequestParam(required = false) ClaimStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<ClaimResponse> response = claimService.getClaimsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/users")
    @Operation(summary = "List all platform users")
    public ResponseEntity<ApiResponse<PageResponse<UserSummaryDto>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<UserSummaryDto> response = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/users/{id}/status")
    @Operation(summary = "Activate or deactivate user account")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, Boolean> statusMap) {
        boolean active = statusMap != null ? statusMap.getOrDefault("active", true) : true;
        adminService.setUserActiveStatus(id, active);
        return ResponseEntity.ok(ApiResponse.ok(null, "User status updated to: " + (active ? "ACTIVE" : "INACTIVE")));
    }

    @PatchMapping("/users/{id}/role")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @Operation(summary = "Super Admin: Update user role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> roleMap) {
        String roleStr = roleMap.get("role");
        if (roleStr != null) {
            adminService.updateUserRole(id, com.findit.enums.Role.valueOf(roleStr));
        }
        return ResponseEntity.ok(ApiResponse.ok(null, "User role updated successfully"));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @Operation(summary = "Super Admin: View system audit trail")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        PageResponse<AuditLogResponse> response = adminService.getAuditLogs(pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
