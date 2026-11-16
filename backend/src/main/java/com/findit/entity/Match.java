package com.findit.entity;

import com.findit.enums.MatchClassification;
import com.findit.enums.MatchStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "matches", uniqueConstraints = {
    @UniqueConstraint(name = "uq_lost_found_match", columnNames = {"lost_item_id", "found_item_id"})
}, indexes = {
    @Index(name = "idx_matches_lost_item", columnList = "lost_item_id"),
    @Index(name = "idx_matches_found_item", columnList = "found_item_id"),
    @Index(name = "idx_matches_score", columnList = "match_score"),
    @Index(name = "idx_matches_status", columnList = "status")
})
public class Match extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lost_item_id", nullable = false)
    private LostItem lostItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "found_item_id", nullable = false)
    private FoundItem foundItem;

    @Column(name = "match_score", nullable = false)
    private Double matchScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MatchClassification classification;

    @Column(name = "match_reasons", columnDefinition = "TEXT", nullable = false)
    private String matchReasons;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MatchStatus status = MatchStatus.UNVIEWED;

    public Match() {
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private LostItem lostItem;
        private FoundItem foundItem;
        private Double matchScore;
        private MatchClassification classification;
        private String matchReasons;
        private MatchStatus status = MatchStatus.UNVIEWED;

        public Builder lostItem(LostItem lostItem) { this.lostItem = lostItem; return this; }
        public Builder foundItem(FoundItem foundItem) { this.foundItem = foundItem; return this; }
        public Builder matchScore(Double matchScore) { this.matchScore = matchScore; return this; }
        public Builder classification(MatchClassification classification) { this.classification = classification; return this; }
        public Builder matchReasons(String matchReasons) { this.matchReasons = matchReasons; return this; }
        public Builder status(MatchStatus status) { this.status = status; return this; }

        public Match build() {
            Match m = new Match();
            m.setLostItem(lostItem);
            m.setFoundItem(foundItem);
            m.setMatchScore(matchScore);
            m.setClassification(classification);
            m.setMatchReasons(matchReasons);
            m.setStatus(status);
            return m;
        }
    }

    public LostItem getLostItem() { return lostItem; }
    public void setLostItem(LostItem lostItem) { this.lostItem = lostItem; }
    public FoundItem getFoundItem() { return foundItem; }
    public void setFoundItem(FoundItem foundItem) { this.foundItem = foundItem; }
    public Double getMatchScore() { return matchScore; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }
    public MatchClassification getClassification() { return classification; }
    public void setClassification(MatchClassification classification) { this.classification = classification; }
    public String getMatchReasons() { return matchReasons; }
    public void setMatchReasons(String matchReasons) { this.matchReasons = matchReasons; }
    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }
}
