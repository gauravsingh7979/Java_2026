package com.findit.entity;

import com.findit.enums.RecoveryStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recoveries", indexes = {
    @Index(name = "idx_recoveries_code", columnList = "recovery_code", unique = true),
    @Index(name = "idx_recoveries_claim", columnList = "claim_id", unique = true),
    @Index(name = "idx_recoveries_status", columnList = "status"),
    @Index(name = "idx_recoveries_claimant", columnList = "claimant_id"),
    @Index(name = "idx_recoveries_finder", columnList = "finder_id")
})
public class Recovery extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false, unique = true)
    private Claim claim;

    @Column(name = "recovery_code", nullable = false, unique = true, length = 30)
    private String recoveryCode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claimant_id", nullable = false)
    private User claimant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "finder_id", nullable = false)
    private User finder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RecoveryStatus status = RecoveryStatus.INITIATED;

    @Column(name = "handover_scheduled_at")
    private LocalDateTime handoverScheduledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "handover_notes", columnDefinition = "TEXT")
    private String handoverNotes;

    public Recovery() {
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Claim claim;
        private String recoveryCode;
        private User claimant;
        private User finder;
        private RecoveryStatus status = RecoveryStatus.INITIATED;
        private LocalDateTime handoverScheduledAt;
        private LocalDateTime completedAt;
        private String handoverNotes;

        public Builder claim(Claim claim) { this.claim = claim; return this; }
        public Builder recoveryCode(String recoveryCode) { this.recoveryCode = recoveryCode; return this; }
        public Builder claimant(User claimant) { this.claimant = claimant; return this; }
        public Builder finder(User finder) { this.finder = finder; return this; }
        public Builder status(RecoveryStatus status) { this.status = status; return this; }
        public Builder handoverScheduledAt(LocalDateTime time) { this.handoverScheduledAt = time; return this; }
        public Builder completedAt(LocalDateTime time) { this.completedAt = time; return this; }
        public Builder handoverNotes(String notes) { this.handoverNotes = notes; return this; }

        public Recovery build() {
            Recovery r = new Recovery();
            r.setClaim(claim);
            r.setRecoveryCode(recoveryCode);
            r.setClaimant(claimant);
            r.setFinder(finder);
            r.setStatus(status);
            r.setHandoverScheduledAt(handoverScheduledAt);
            r.setCompletedAt(completedAt);
            r.setHandoverNotes(handoverNotes);
            return r;
        }
    }

    public Claim getClaim() { return claim; }
    public void setClaim(Claim claim) { this.claim = claim; }
    public String getRecoveryCode() { return recoveryCode; }
    public void setRecoveryCode(String recoveryCode) { this.recoveryCode = recoveryCode; }
    public User getClaimant() { return claimant; }
    public void setClaimant(User claimant) { this.claimant = claimant; }
    public User getFinder() { return finder; }
    public void setFinder(User finder) { this.finder = finder; }
    public RecoveryStatus getStatus() { return status; }
    public void setStatus(RecoveryStatus status) { this.status = status; }
    public LocalDateTime getHandoverScheduledAt() { return handoverScheduledAt; }
    public void setHandoverScheduledAt(LocalDateTime handoverScheduledAt) { this.handoverScheduledAt = handoverScheduledAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public String getHandoverNotes() { return handoverNotes; }
    public void setHandoverNotes(String handoverNotes) { this.handoverNotes = handoverNotes; }
}
