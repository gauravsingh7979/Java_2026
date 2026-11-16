package com.findit.service.impl;

import com.findit.dto.claim.ClaimRequest;
import com.findit.dto.claim.ClaimResponse;
import com.findit.dto.claim.ClaimReviewRequest;
import com.findit.dto.common.PageResponse;
import com.findit.dto.user.UserSummaryDto;
import com.findit.entity.Claim;
import com.findit.entity.FoundItem;
import com.findit.entity.Recovery;
import com.findit.entity.User;
import com.findit.enums.ClaimStatus;
import com.findit.enums.ItemStatus;
import com.findit.enums.Role;
import com.findit.exception.BadRequestException;
import com.findit.exception.ForbiddenException;
import com.findit.exception.InvalidClaimException;
import com.findit.exception.ResourceNotFoundException;
import com.findit.mapper.ItemMapper;
import com.findit.repository.ClaimRepository;
import com.findit.repository.FoundItemRepository;
import com.findit.repository.UserRepository;
import com.findit.security.SecurityUtils;
import com.findit.service.AuditService;
import com.findit.service.ClaimService;
import com.findit.service.NotificationService;
import com.findit.service.RecoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ClaimServiceImpl implements ClaimService {

    private static final Logger log = LoggerFactory.getLogger(ClaimServiceImpl.class);

    private final ClaimRepository claimRepository;
    private final FoundItemRepository foundItemRepository;
    private final UserRepository userRepository;
    private final RecoveryService recoveryService;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final ItemMapper itemMapper;

    public ClaimServiceImpl(ClaimRepository claimRepository,
                            FoundItemRepository foundItemRepository,
                            UserRepository userRepository,
                            RecoveryService recoveryService,
                            NotificationService notificationService,
                            AuditService auditService,
                            ItemMapper itemMapper) {
        this.claimRepository = claimRepository;
        this.foundItemRepository = foundItemRepository;
        this.userRepository = userRepository;
        this.recoveryService = recoveryService;
        this.notificationService = notificationService;
        this.auditService = auditService;
        this.itemMapper = itemMapper;
    }

    @Override
    @Transactional
    public ClaimResponse submitClaim(ClaimRequest request, Long claimantId) {
        User claimant = userRepository.findById(claimantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + claimantId));

        FoundItem item = foundItemRepository.findById(request.getFoundItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Found item not found with id: " + request.getFoundItemId()));

        if (item.getUser().getId().equals(claimantId)) {
            throw new InvalidClaimException("You cannot submit an ownership claim for an item you reported found");
        }

        if (item.getStatus() == ItemStatus.RECOVERED || item.getStatus() == ItemStatus.CLAIMED) {
            throw new InvalidClaimException("This item has already been claimed or recovered");
        }

        boolean alreadyClaimed = claimRepository.existsByFoundItemIdAndClaimantIdAndStatusIn(
                item.getId(), claimantId, Set.of(ClaimStatus.PENDING, ClaimStatus.UNDER_REVIEW, ClaimStatus.APPROVED));
        if (alreadyClaimed) {
            throw new InvalidClaimException("You already have an active claim submitted for this item");
        }

        Claim claim = Claim.builder()
                .foundItem(item)
                .claimant(claimant)
                .ownershipExplanation(request.getOwnershipExplanation().trim())
                .distinctiveCharacteristics(request.getDistinctiveCharacteristics().trim())
                .supportingDocumentUrl(request.getSupportingDocumentUrl())
                .status(ClaimStatus.PENDING)
                .build();

        Claim saved = claimRepository.save(claim);

        if (item.getStatus() == ItemStatus.ACTIVE) {
            item.setStatus(ItemStatus.PENDING_CLAIM);
            foundItemRepository.save(item);
        }

        auditService.logAction(claimant, "SUBMIT_CLAIM", "Claim", saved.getId(),
                "Submitted claim for found item: " + item.getTitle(), null);

        notificationService.createNotification(
                claimant,
                "Claim Submitted Successfully",
                "Your ownership claim for '" + item.getTitle() + "' has been received and is pending administrator review.",
                "CLAIM_UPDATE",
                "/claims"
        );

        userRepository.findByRole(Role.ROLE_ADMIN, Pageable.unpaged()).forEach(admin -> {
            notificationService.createNotification(
                    admin,
                    "New Ownership Claim Awaiting Review",
                    "A new claim on item '" + item.getTitle() + "' requires verification.",
                    "CLAIM_UPDATE",
                    "/admin/claims"
            );
        });

        return toResponse(saved, claimantId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClaimResponse> getMyClaims(Long claimantId, Pageable pageable) {
        Page<Claim> page = claimRepository.findByClaimantIdOrderByCreatedAtDesc(claimantId, pageable);
        return PageResponse.from(page.map(c -> toResponse(c, claimantId)));
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimResponse getClaimById(Long claimId, Long currentUserId) {
        Claim claim = getClaim(claimId);
        boolean isClaimant = claim.getClaimant().getId().equals(currentUserId);
        boolean isFinder = claim.getFoundItem().getUser().getId().equals(currentUserId);
        boolean isAdmin = SecurityUtils.isAdminOrSuperAdmin();

        if (!isClaimant && !isFinder && !isAdmin) {
            throw new ForbiddenException("You are not authorized to view this claim");
        }

        return toResponse(claim, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClaimResponse> getClaimsByStatus(ClaimStatus status, Pageable pageable) {
        Page<Claim> page = status != null ?
                claimRepository.findByStatus(status, pageable) :
                claimRepository.findAll(pageable);
        return PageResponse.from(page.map(c -> toResponse(c, null)));
    }

    @Override
    @Transactional
    public ClaimResponse markUnderReview(Long claimId, Long adminId) {
        Claim claim = getClaim(claimId);
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        claim.setStatus(ClaimStatus.UNDER_REVIEW);
        claim.setReviewer(admin);
        Claim saved = claimRepository.save(claim);

        auditService.logAction(admin, "CLAIM_UNDER_REVIEW", "Claim", claimId, "Marked claim as UNDER_REVIEW", null);

        notificationService.createNotification(
                claim.getClaimant(),
                "Claim Under Review",
                "Your ownership claim for '" + claim.getFoundItem().getTitle() + "' is currently being reviewed by an administrator.",
                "CLAIM_UPDATE",
                "/claims"
        );

        return toResponse(saved, null);
    }

    @Override
    @Transactional
    public ClaimResponse approveClaim(Long claimId, ClaimReviewRequest request, Long adminId) {
        Claim claim = getClaim(claimId);
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        if (claim.getStatus() == ClaimStatus.APPROVED) {
            throw new BadRequestException("This claim is already approved");
        }

        claim.setStatus(ClaimStatus.APPROVED);
        claim.setReviewer(admin);
        claim.setAdminDecisionReason(request.getDecisionReason().trim());
        claim.setReviewedAt(LocalDateTime.now());

        FoundItem foundItem = claim.getFoundItem();
        foundItem.setStatus(ItemStatus.CLAIMED);
        foundItemRepository.save(foundItem);

        Claim savedClaim = claimRepository.save(claim);

        Recovery recovery = recoveryService.initiateRecovery(savedClaim);
        savedClaim.setRecovery(recovery);

        auditService.logAction(admin, "APPROVE_CLAIM", "Claim", claimId,
                "Approved claim. Reason: " + request.getDecisionReason(), null);

        return toResponse(savedClaim, null);
    }

    @Override
    @Transactional
    public ClaimResponse rejectClaim(Long claimId, ClaimReviewRequest request, Long adminId) {
        Claim claim = getClaim(claimId);
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        claim.setStatus(ClaimStatus.REJECTED);
        claim.setReviewer(admin);
        claim.setAdminDecisionReason(request.getDecisionReason().trim());
        claim.setReviewedAt(LocalDateTime.now());

        FoundItem foundItem = claim.getFoundItem();
        List<Claim> otherClaims = claimRepository.findByFoundItemId(foundItem.getId());
        boolean hasOtherActive = otherClaims.stream()
                .anyMatch(c -> !c.getId().equals(claimId) && (c.getStatus() == ClaimStatus.PENDING || c.getStatus() == ClaimStatus.UNDER_REVIEW));

        if (!hasOtherActive && foundItem.getStatus() == ItemStatus.PENDING_CLAIM) {
            foundItem.setStatus(ItemStatus.ACTIVE);
            foundItemRepository.save(foundItem);
        }

        Claim saved = claimRepository.save(claim);

        auditService.logAction(admin, "REJECT_CLAIM", "Claim", claimId,
                "Rejected claim. Reason: " + request.getDecisionReason(), null);

        notificationService.createNotification(
                claim.getClaimant(),
                "Claim Status Update: Rejected",
                "Your ownership claim for '" + foundItem.getTitle() + "' was not approved. Reason: " + request.getDecisionReason(),
                "CLAIM_UPDATE",
                "/claims"
        );

        return toResponse(saved, null);
    }

    private Claim getClaim(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
    }

    private ClaimResponse toResponse(Claim c, Long currentUserId) {
        boolean canSeePrivate = SecurityUtils.isAdminOrSuperAdmin() ||
                                (currentUserId != null && c.getFoundItem().getUser().getId().equals(currentUserId));

        String recoveryCode = null;
        Long recoveryId = null;
        if (c.getRecovery() != null) {
            recoveryId = c.getRecovery().getId();
            if (SecurityUtils.isAdminOrSuperAdmin() ||
                (currentUserId != null && (c.getClaimant().getId().equals(currentUserId) || c.getFoundItem().getUser().getId().equals(currentUserId)))) {
                recoveryCode = c.getRecovery().getRecoveryCode();
            }
        }

        return ClaimResponse.builder()
                .id(c.getId())
                .foundItemId(c.getFoundItem().getId())
                .foundItem(itemMapper.toFoundItemResponse(c.getFoundItem(), canSeePrivate))
                .claimant(toSummary(c.getClaimant()))
                .reviewer(c.getReviewer() != null ? toSummary(c.getReviewer()) : null)
                .ownershipExplanation(c.getOwnershipExplanation())
                .distinctiveCharacteristics(c.getDistinctiveCharacteristics())
                .supportingDocumentUrl(c.getSupportingDocumentUrl())
                .status(c.getStatus())
                .adminDecisionReason(c.getAdminDecisionReason())
                .reviewedAt(c.getReviewedAt())
                .recoveryId(recoveryId)
                .recoveryCode(recoveryCode)
                .createdAt(c.getCreatedAt())
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
