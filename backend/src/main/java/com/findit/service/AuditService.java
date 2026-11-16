package com.findit.service;

import com.findit.dto.admin.AuditLogResponse;
import com.findit.dto.common.PageResponse;
import com.findit.entity.User;
import org.springframework.data.domain.Pageable;

public interface AuditService {

    void logAction(User actor, String action, String entityName, Long entityId, String details, String ipAddress);

    void logAction(String action, String entityName, Long entityId, String details);

    PageResponse<AuditLogResponse> getAuditLogs(Pageable pageable);
}
