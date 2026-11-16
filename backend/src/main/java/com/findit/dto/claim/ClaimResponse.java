package com.findit.dto.claim;

import com.findit.dto.item.FoundItemResponse;
import com.findit.dto.user.UserSummaryDto;
import com.findit.enums.ClaimStatus;

import java.time.LocalDateTime;

public class ClaimResponse {



    private Long id;
    private Long foundItemId;
    private FoundItemResponse foundItem;
    private UserSummaryDto claimant;
    private UserSummaryDto reviewer;
    private String ownershipExplanation;
    private String distinctiveCharacteristics;
    private String supportingDocumentUrl;
    private ClaimStatus status;
    private String adminDecisionReason;
    private LocalDateTime reviewedAt;
    private Long recoveryId;
    private String recoveryCode; // Only visible if approved and caller is claimant or finder/admin
    private LocalDateTime createdAt;


    // Default constructor
    public ClaimResponse() {}

    // All-args constructor
    public ClaimResponse(Long id, Long foundItemId, FoundItemResponse foundItem, UserSummaryDto claimant, UserSummaryDto reviewer, String ownershipExplanation, String distinctiveCharacteristics, String supportingDocumentUrl, ClaimStatus status, String adminDecisionReason, LocalDateTime reviewedAt, Long recoveryId, String recoveryCode, LocalDateTime createdAt) {
        this.id = id;
        this.foundItemId = foundItemId;
        this.foundItem = foundItem;
        this.claimant = claimant;
        this.reviewer = reviewer;
        this.ownershipExplanation = ownershipExplanation;
        this.distinctiveCharacteristics = distinctiveCharacteristics;
        this.supportingDocumentUrl = supportingDocumentUrl;
        this.status = status;
        this.adminDecisionReason = adminDecisionReason;
        this.reviewedAt = reviewedAt;
        this.recoveryId = recoveryId;
        this.recoveryCode = recoveryCode;
        this.createdAt = createdAt;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long foundItemId;
        private FoundItemResponse foundItem;
        private UserSummaryDto claimant;
        private UserSummaryDto reviewer;
        private String ownershipExplanation;
        private String distinctiveCharacteristics;
        private String supportingDocumentUrl;
        private ClaimStatus status;
        private String adminDecisionReason;
        private LocalDateTime reviewedAt;
        private Long recoveryId;
        private String recoveryCode;
        private LocalDateTime createdAt;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder foundItemId(Long foundItemId) { this.foundItemId = foundItemId; return this; }
        public Builder foundItem(FoundItemResponse foundItem) { this.foundItem = foundItem; return this; }
        public Builder claimant(UserSummaryDto claimant) { this.claimant = claimant; return this; }
        public Builder reviewer(UserSummaryDto reviewer) { this.reviewer = reviewer; return this; }
        public Builder ownershipExplanation(String ownershipExplanation) { this.ownershipExplanation = ownershipExplanation; return this; }
        public Builder distinctiveCharacteristics(String distinctiveCharacteristics) { this.distinctiveCharacteristics = distinctiveCharacteristics; return this; }
        public Builder supportingDocumentUrl(String supportingDocumentUrl) { this.supportingDocumentUrl = supportingDocumentUrl; return this; }
        public Builder status(ClaimStatus status) { this.status = status; return this; }
        public Builder adminDecisionReason(String adminDecisionReason) { this.adminDecisionReason = adminDecisionReason; return this; }
        public Builder reviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; return this; }
        public Builder recoveryId(Long recoveryId) { this.recoveryId = recoveryId; return this; }
        public Builder recoveryCode(String recoveryCode) { this.recoveryCode = recoveryCode; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ClaimResponse build() {
            return new ClaimResponse(id, foundItemId, foundItem, claimant, reviewer, ownershipExplanation, distinctiveCharacteristics, supportingDocumentUrl, status, adminDecisionReason, reviewedAt, recoveryId, recoveryCode, createdAt);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFoundItemId() { return foundItemId; }
    public void setFoundItemId(Long foundItemId) { this.foundItemId = foundItemId; }
    public FoundItemResponse getFoundItem() { return foundItem; }
    public void setFoundItem(FoundItemResponse foundItem) { this.foundItem = foundItem; }
    public UserSummaryDto getClaimant() { return claimant; }
    public void setClaimant(UserSummaryDto claimant) { this.claimant = claimant; }
    public UserSummaryDto getReviewer() { return reviewer; }
    public void setReviewer(UserSummaryDto reviewer) { this.reviewer = reviewer; }
    public String getOwnershipExplanation() { return ownershipExplanation; }
    public void setOwnershipExplanation(String ownershipExplanation) { this.ownershipExplanation = ownershipExplanation; }
    public String getDistinctiveCharacteristics() { return distinctiveCharacteristics; }
    public void setDistinctiveCharacteristics(String distinctiveCharacteristics) { this.distinctiveCharacteristics = distinctiveCharacteristics; }
    public String getSupportingDocumentUrl() { return supportingDocumentUrl; }
    public void setSupportingDocumentUrl(String supportingDocumentUrl) { this.supportingDocumentUrl = supportingDocumentUrl; }
    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }
    public String getAdminDecisionReason() { return adminDecisionReason; }
    public void setAdminDecisionReason(String adminDecisionReason) { this.adminDecisionReason = adminDecisionReason; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public Long getRecoveryId() { return recoveryId; }
    public void setRecoveryId(Long recoveryId) { this.recoveryId = recoveryId; }
    public String getRecoveryCode() { return recoveryCode; }
    public void setRecoveryCode(String recoveryCode) { this.recoveryCode = recoveryCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

}
