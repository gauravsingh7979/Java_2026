package com.findit.service.impl;

import com.findit.dto.admin.AuditLogResponse;
import com.findit.dto.common.PageResponse;
import com.findit.entity.AuditLog;
import com.findit.entity.User;
import com.findit.repository.AuditLogRepository;
import com.findit.security.SecurityUtils;
import com.findit.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuditServiceImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    private final AuditLogRepository auditLogRepository;

    public AuditServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void logAction(User actor, String action, String entityName, Long entityId, String details, String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
                .actor(actor)
                .actorEmail(actor != null ? actor.getEmail() : "SYSTEM")
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .details(details)
                .ipAddress(ipAddress != null ? ipAddress : "127.0.0.1")
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
        log.info("[AUDIT] Action: {} on {}:{} by {}", action, entityName, entityId, auditLog.getActorEmail());
    }

    @Override
    @Transactional
    public void logAction(String action, String entityName, Long entityId, String details) {
        String actorEmail = SecurityUtils.getCurrentUserPrincipal()
                .map(p -> p.getEmail())
                .orElse("SYSTEM");

        AuditLog auditLog = AuditLog.builder()
                .actorEmail(actorEmail)
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .details(details)
                .ipAddress("127.0.0.1")
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
        log.info("[AUDIT] Action: {} on {}:{} by {}", action, entityName, entityId, actorEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AuditLogResponse> getAuditLogs(Pageable pageable) {
        Page<AuditLog> page = auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        return PageResponse.from(page.map(this::mapToResponse));
    }

    private AuditLogResponse mapToResponse(AuditLog l) {
        return AuditLogResponse.builder()
                .id(l.getId())
                .actorId(l.getActor() != null ? l.getActor().getId() : null)
                .actorEmail(l.getActorEmail())
                .action(l.getAction())
                .entityName(l.getEntityName())
                .entityId(l.getEntityId())
                .details(l.getDetails())
                .ipAddress(l.getIpAddress())
                .createdAt(l.getCreatedAt())
                .build();
    }
}
