package com.findit.service.impl;

import com.findit.dto.common.PageResponse;
import com.findit.dto.recovery.ConfirmHandoverRequest;
import com.findit.dto.recovery.RecoveryResponse;
import com.findit.dto.recovery.ScheduleHandoverRequest;
import com.findit.dto.user.UserSummaryDto;
import com.findit.entity.Claim;
import com.findit.entity.FoundItem;
import com.findit.entity.Recovery;
import com.findit.entity.User;
import com.findit.enums.ItemStatus;
import com.findit.enums.RecoveryStatus;
import com.findit.exception.BadRequestException;
import com.findit.exception.ForbiddenException;
import com.findit.exception.ResourceNotFoundException;
import com.findit.mapper.ItemMapper;
import com.findit.repository.FoundItemRepository;
import com.findit.repository.RecoveryRepository;
import com.findit.security.SecurityUtils;
import com.findit.service.AuditService;
import com.findit.service.NotificationService;
import com.findit.service.RecoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class RecoveryServiceImpl implements RecoveryService {

    private static final Logger log = LoggerFactory.getLogger(RecoveryServiceImpl.class);

    private final RecoveryRepository recoveryRepository;
    private final FoundItemRepository foundItemRepository;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final ItemMapper itemMapper;

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public RecoveryServiceImpl(RecoveryRepository recoveryRepository,
                               FoundItemRepository foundItemRepository,
                               NotificationService notificationService,
                               AuditService auditService,
                               ItemMapper itemMapper) {
        this.recoveryRepository = recoveryRepository;
        this.foundItemRepository = foundItemRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional
    public Recovery initiateRecovery(Claim approvedClaim) {
        String recoveryCode = generateRecoveryCode();

        Recovery recovery = Recovery.builder()
                .claim(approvedClaim)
                .recoveryCode(recoveryCode)
                .claimant(approvedClaim.getClaimant())
                .finder(approvedClaim.getFoundItem().getUser())
                .status(RecoveryStatus.INITIATED)
                .build();

        Recovery saved = recoveryRepository.save(recovery);

        auditService.logAction("INITIATE_RECOVERY", "Recovery", saved.getId(),
                "Recovery initiated for item: " + approvedClaim.getFoundItem().getTitle());

        notificationService.createNotification(
                approvedClaim.getClaimant(),
                "Claim Approved! Recovery Code Generated",
                "Your ownership claim for '" + approvedClaim.getFoundItem().getTitle() + "' was approved! Your recovery reference code is " + recoveryCode + ". Present this code during handover.",
                "RECOVERY_UPDATE",
                "/claims"
        );

        notificationService.createNotification(
                approvedClaim.getFoundItem().getUser(),
                "Handover Required: Claim Approved",
                "A claim on '" + approvedClaim.getFoundItem().getTitle() + "' was verified by an admin. Coordinate handover with the claimant using reference " + recoveryCode + ".",
                "RECOVERY_UPDATE",
                "/claims"
        );

        return saved;
    }

    @Override
    @Transactional
    public RecoveryResponse scheduleHandover(Long recoveryId, ScheduleHandoverRequest request, Long userId) {
        Recovery recovery = getRecovery(recoveryId);
        validateParticipant(recovery, userId);

        LocalDateTime scheduledTime = request.getEffectiveScheduledAt();
        if (scheduledTime == null) {
            scheduledTime = LocalDateTime.now().plusDays(1);
        }
        recovery.setHandoverScheduledAt(scheduledTime);
        String notes = request.getEffectiveNotes();
        if (notes != null && !notes.isBlank()) {
            recovery.setHandoverNotes(notes);
        }
        recovery.setStatus(RecoveryStatus.HANDOVER_SCHEDULED);
        Recovery saved = recoveryRepository.save(recovery);

        User notifyTarget = recovery.getClaimant().getId().equals(userId) ? recovery.getFinder() : recovery.getClaimant();
        notificationService.createNotification(
                notifyTarget,
                "Handover Scheduled",
                "Handover for '" + recovery.getClaim().getFoundItem().getTitle() + "' has been scheduled for " + scheduledTime + ".",
                "RECOVERY_UPDATE",
                "/claims"
        );

        auditService.logAction("SCHEDULE_HANDOVER", "Recovery", recoveryId, "Handover scheduled for: " + request.getScheduledAt());

        return toResponse(saved, userId);
    }

    @Override
    @Transactional
    public RecoveryResponse confirmHandover(Long recoveryId, ConfirmHandoverRequest request, Long userId) {
        Recovery recovery = getRecovery(recoveryId);
        validateParticipant(recovery, userId);

        if (recovery.getStatus() == RecoveryStatus.COMPLETED) {
            throw new BadRequestException("This item has already been marked as recovered");
        }

        String providedCode = request.getRecoveryCode().trim().toUpperCase();
        if (!recovery.getRecoveryCode().equalsIgnoreCase(providedCode)) {
            throw new BadRequestException("Invalid recovery code. Verification failed.");
        }

        recovery.setStatus(RecoveryStatus.COMPLETED);
        recovery.setCompletedAt(LocalDateTime.now());
        if (request.getHandoverNotes() != null) {
            String existing = recovery.getHandoverNotes() != null ? recovery.getHandoverNotes() + "\n" : "";
            recovery.setHandoverNotes(existing + "Confirmation notes: " + request.getHandoverNotes());
        }

        Recovery saved = recoveryRepository.save(recovery);

        FoundItem foundItem = recovery.getClaim().getFoundItem();
        foundItem.setStatus(ItemStatus.RECOVERED);
        foundItemRepository.save(foundItem);

        auditService.logAction("CONFIRM_RECOVERY", "Recovery", recoveryId,
                "Item '" + foundItem.getTitle() + "' successfully handed over and recovered");

        notificationService.createNotification(
                recovery.getClaimant(),
                "Item Successfully Recovered!",
                "Handover confirmed for '" + foundItem.getTitle() + "'. The item has been marked as RECOVERED.",
                "RECOVERY_UPDATE",
                "/claims"
        );

        notificationService.createNotification(
                recovery.getFinder(),
                "Handover Completed",
                "Thank you for helping recover '" + foundItem.getTitle() + "'! The transaction is marked as RECOVERED.",
                "RECOVERY_UPDATE",
                "/claims"
        );

        return toResponse(saved, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RecoveryResponse> getUserRecoveries(Long userId, Pageable pageable) {
        Page<Recovery> page = recoveryRepository.findUserRecoveries(userId, pageable);
        return PageResponse.from(page.map(r -> toResponse(r, userId)));
    }

    @Override
    @Transactional(readOnly = true)
    public RecoveryResponse getRecoveryById(Long recoveryId, Long userId) {
        Recovery recovery = getRecovery(recoveryId);
        validateParticipant(recovery, userId);
        return toResponse(recovery, userId);
    }

    private Recovery getRecovery(Long id) {
        return recoveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recovery not found with id: " + id));
    }

    private void validateParticipant(Recovery recovery, Long userId) {
        boolean isParticipant = recovery.getClaimant().getId().equals(userId) ||
                                recovery.getFinder().getId().equals(userId);
        if (!isParticipant && !SecurityUtils.isAdminOrSuperAdmin()) {
            throw new ForbiddenException("You are not authorized to view or manage this recovery process");
        }
    }

    private String generateRecoveryCode() {
        StringBuilder sb = new StringBuilder("RCV-");
        for (int i = 0; i < 4; i++) {
            sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }
        sb.append("-");
        for (int i = 0; i < 4; i++) {
            sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
        }
        return sb.toString();
    }

    private RecoveryResponse toResponse(Recovery r, Long userId) {
        boolean canSeePrivate = r.getClaimant().getId().equals(userId) ||
                                r.getFinder().getId().equals(userId) ||
                                SecurityUtils.isAdminOrSuperAdmin();

        return RecoveryResponse.builder()
                .id(r.getId())
                .claimId(r.getClaim().getId())
                .recoveryCode(r.getRecoveryCode())
                .item(itemMapper.toFoundItemResponse(r.getClaim().getFoundItem(), canSeePrivate))
                .claimant(toSummary(r.getClaimant()))
                .finder(toSummary(r.getFinder()))
                .status(r.getStatus())
                .handoverScheduledAt(r.getHandoverScheduledAt())
                .completedAt(r.getCompletedAt())
                .handoverNotes(r.getHandoverNotes())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private UserSummaryDto toSummary(User u) {
        return UserSummaryDto.builder()
                .id(u.getId())
                .email(u.getEmail())
                .firstName(u.getFirstName())
                .lastName(u.getLastName())
                .phoneNumber(u.getPhoneNumber())
                .role(u.getRole())
                .active(u.isActive())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
