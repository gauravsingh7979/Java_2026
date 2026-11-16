package com.findit.entity;

import com.findit.enums.ClaimStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "claims", indexes = {
    @Index(name = "idx_claims_found_item", columnList = "found_item_id"),
    @Index(name = "idx_claims_claimant", columnList = "claimant_id"),
    @Index(name = "idx_claims_status", columnList = "status")
})
public class Claim extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "found_item_id", nullable = false)
    private FoundItem foundItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claimant_id", nullable = false)
    private User claimant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @Column(name = "ownership_explanation", columnDefinition = "TEXT", nullable = false)
    private String ownershipExplanation;

    @Column(name = "distinctive_characteristics", columnDefinition = "TEXT", nullable = false)
    private String distinctiveCharacteristics;

    @Column(name = "supporting_document_url", length = 500)
    private String supportingDocumentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(name = "admin_decision_reason", columnDefinition = "TEXT")
    private String adminDecisionReason;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @OneToOne(mappedBy = "claim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Recovery recovery;

    public Claim() {
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private FoundItem foundItem;
        private User claimant;
        private User reviewer;
        private String ownershipExplanation;
        private String distinctiveCharacteristics;
        private String supportingDocumentUrl;
        private ClaimStatus status = ClaimStatus.PENDING;
        private String adminDecisionReason;
        private LocalDateTime reviewedAt;

        public Builder foundItem(FoundItem foundItem) { this.foundItem = foundItem; return this; }
        public Builder claimant(User claimant) { this.claimant = claimant; return this; }
        public Builder reviewer(User reviewer) { this.reviewer = reviewer; return this; }
        public Builder ownershipExplanation(String ownershipExplanation) { this.ownershipExplanation = ownershipExplanation; return this; }
        public Builder distinctiveCharacteristics(String distinctiveCharacteristics) { this.distinctiveCharacteristics = distinctiveCharacteristics; return this; }
        public Builder supportingDocumentUrl(String supportingDocumentUrl) { this.supportingDocumentUrl = supportingDocumentUrl; return this; }
        public Builder status(ClaimStatus status) { this.status = status; return this; }
        public Builder adminDecisionReason(String adminDecisionReason) { this.adminDecisionReason = adminDecisionReason; return this; }
        public Builder reviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; return this; }

        public Claim build() {
            Claim c = new Claim();
            c.setFoundItem(foundItem);
            c.setClaimant(claimant);
            c.setReviewer(reviewer);
            c.setOwnershipExplanation(ownershipExplanation);
            c.setDistinctiveCharacteristics(distinctiveCharacteristics);
            c.setSupportingDocumentUrl(supportingDocumentUrl);
            c.setStatus(status);
            c.setAdminDecisionReason(adminDecisionReason);
            c.setReviewedAt(reviewedAt);
            return c;
        }
    }

    public FoundItem getFoundItem() { return foundItem; }
    public void setFoundItem(FoundItem foundItem) { this.foundItem = foundItem; }
    public User getClaimant() { return claimant; }
    public void setClaimant(User claimant) { this.claimant = claimant; }
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }
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
    public Recovery getRecovery() { return recovery; }
    public void setRecovery(Recovery recovery) { this.recovery = recovery; }
}
