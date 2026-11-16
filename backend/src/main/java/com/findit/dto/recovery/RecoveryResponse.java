package com.findit.dto.recovery;

import com.findit.dto.item.FoundItemResponse;
import com.findit.dto.user.UserSummaryDto;
import com.findit.enums.RecoveryStatus;

import java.time.LocalDateTime;

public class RecoveryResponse {



    private Long id;
    private Long claimId;
    private String recoveryCode;
    private FoundItemResponse item;
    private UserSummaryDto claimant;
    private UserSummaryDto finder;
    private RecoveryStatus status;
    private LocalDateTime handoverScheduledAt;
    private LocalDateTime completedAt;
    private String handoverNotes;
    private LocalDateTime createdAt;


    // Default constructor
    public RecoveryResponse() {}

    // All-args constructor
    public RecoveryResponse(Long id, Long claimId, String recoveryCode, FoundItemResponse item, UserSummaryDto claimant, UserSummaryDto finder, RecoveryStatus status, LocalDateTime handoverScheduledAt, LocalDateTime completedAt, String handoverNotes, LocalDateTime createdAt) {
        this.id = id;
        this.claimId = claimId;
        this.recoveryCode = recoveryCode;
        this.item = item;
        this.claimant = claimant;
        this.finder = finder;
        this.status = status;
        this.handoverScheduledAt = handoverScheduledAt;
        this.completedAt = completedAt;
        this.handoverNotes = handoverNotes;
        this.createdAt = createdAt;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long claimId;
        private String recoveryCode;
        private FoundItemResponse item;
        private UserSummaryDto claimant;
        private UserSummaryDto finder;
        private RecoveryStatus status;
        private LocalDateTime handoverScheduledAt;
        private LocalDateTime completedAt;
        private String handoverNotes;
        private LocalDateTime createdAt;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder claimId(Long claimId) { this.claimId = claimId; return this; }
        public Builder recoveryCode(String recoveryCode) { this.recoveryCode = recoveryCode; return this; }
        public Builder item(FoundItemResponse item) { this.item = item; return this; }
        public Builder claimant(UserSummaryDto claimant) { this.claimant = claimant; return this; }
        public Builder finder(UserSummaryDto finder) { this.finder = finder; return this; }
        public Builder status(RecoveryStatus status) { this.status = status; return this; }
        public Builder handoverScheduledAt(LocalDateTime handoverScheduledAt) { this.handoverScheduledAt = handoverScheduledAt; return this; }
        public Builder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public Builder handoverNotes(String handoverNotes) { this.handoverNotes = handoverNotes; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public RecoveryResponse build() {
            return new RecoveryResponse(id, claimId, recoveryCode, item, claimant, finder, status, handoverScheduledAt, completedAt, handoverNotes, createdAt);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClaimId() { return claimId; }
    public void setClaimId(Long claimId) { this.claimId = claimId; }
    public String getRecoveryCode() { return recoveryCode; }
    public void setRecoveryCode(String recoveryCode) { this.recoveryCode = recoveryCode; }
    public FoundItemResponse getItem() { return item; }
    public void setItem(FoundItemResponse item) { this.item = item; }
    public UserSummaryDto getClaimant() { return claimant; }
    public void setClaimant(UserSummaryDto claimant) { this.claimant = claimant; }
    public UserSummaryDto getFinder() { return finder; }
    public void setFinder(UserSummaryDto finder) { this.finder = finder; }
    public RecoveryStatus getStatus() { return status; }
    public void setStatus(RecoveryStatus status) { this.status = status; }
    public LocalDateTime getHandoverScheduledAt() { return handoverScheduledAt; }
    public void setHandoverScheduledAt(LocalDateTime handoverScheduledAt) { this.handoverScheduledAt = handoverScheduledAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public String getHandoverNotes() { return handoverNotes; }
    public void setHandoverNotes(String handoverNotes) { this.handoverNotes = handoverNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

}
