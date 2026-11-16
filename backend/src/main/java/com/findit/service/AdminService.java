package com.findit.service;

import com.findit.dto.admin.AdminDashboardStatsDto;
import com.findit.dto.admin.AuditLogResponse;
import com.findit.dto.common.PageResponse;
import com.findit.dto.item.FoundItemResponse;
import com.findit.dto.item.LostItemResponse;
import com.findit.dto.user.UserSummaryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {

    AdminDashboardStatsDto getDashboardStats();

    List<LostItemResponse> getAllLostReportsForModeration();

    List<FoundItemResponse> getAllFoundReportsForModeration();

    PageResponse<UserSummaryDto> getAllUsers(Pageable pageable);

    void setUserActiveStatus(Long userId, boolean active);

    void updateUserRole(Long userId, com.findit.enums.Role role);

    PageResponse<AuditLogResponse> getAuditLogs(Pageable pageable);
}
