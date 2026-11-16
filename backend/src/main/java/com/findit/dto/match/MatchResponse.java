package com.findit.dto.match;

import com.findit.dto.item.FoundItemResponse;
import com.findit.dto.item.LostItemResponse;
import com.findit.enums.MatchClassification;
import com.findit.enums.MatchStatus;

import java.time.LocalDateTime;

public class MatchResponse {



    private Long id;
    private Long lostItemId;
    private Long foundItemId;
    private LostItemResponse lostItem;
    private FoundItemResponse foundItem;
    private Double matchScore;
    private MatchClassification classification;
    private String matchReasons;
    private MatchScoreBreakdownDto scoreBreakdown;
    private MatchStatus status;
    private LocalDateTime createdAt;


    // Default constructor
    public MatchResponse() {}

    // All-args constructor
    public MatchResponse(Long id, Long lostItemId, Long foundItemId, LostItemResponse lostItem, FoundItemResponse foundItem, Double matchScore, MatchClassification classification, String matchReasons, MatchScoreBreakdownDto scoreBreakdown, MatchStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.lostItemId = lostItemId;
        this.foundItemId = foundItemId;
        this.lostItem = lostItem;
        this.foundItem = foundItem;
        this.matchScore = matchScore;
        this.classification = classification;
        this.matchReasons = matchReasons;
        this.scoreBreakdown = scoreBreakdown;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long lostItemId;
        private Long foundItemId;
        private LostItemResponse lostItem;
        private FoundItemResponse foundItem;
        private Double matchScore;
        private MatchClassification classification;
        private String matchReasons;
        private MatchScoreBreakdownDto scoreBreakdown;
        private MatchStatus status;
        private LocalDateTime createdAt;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder lostItemId(Long lostItemId) { this.lostItemId = lostItemId; return this; }
        public Builder foundItemId(Long foundItemId) { this.foundItemId = foundItemId; return this; }
        public Builder lostItem(LostItemResponse lostItem) { this.lostItem = lostItem; return this; }
        public Builder foundItem(FoundItemResponse foundItem) { this.foundItem = foundItem; return this; }
        public Builder matchScore(Double matchScore) { this.matchScore = matchScore; return this; }
        public Builder classification(MatchClassification classification) { this.classification = classification; return this; }
        public Builder matchReasons(String matchReasons) { this.matchReasons = matchReasons; return this; }
        public Builder scoreBreakdown(MatchScoreBreakdownDto scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; return this; }
        public Builder status(MatchStatus status) { this.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public MatchResponse build() {
            return new MatchResponse(id, lostItemId, foundItemId, lostItem, foundItem, matchScore, classification, matchReasons, scoreBreakdown, status, createdAt);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getLostItemId() { return lostItemId; }
    public void setLostItemId(Long lostItemId) { this.lostItemId = lostItemId; }
    public Long getFoundItemId() { return foundItemId; }
    public void setFoundItemId(Long foundItemId) { this.foundItemId = foundItemId; }
    public LostItemResponse getLostItem() { return lostItem; }
    public void setLostItem(LostItemResponse lostItem) { this.lostItem = lostItem; }
    public FoundItemResponse getFoundItem() { return foundItem; }
    public void setFoundItem(FoundItemResponse foundItem) { this.foundItem = foundItem; }
    public Double getMatchScore() { return matchScore; }
    public void setMatchScore(Double matchScore) { this.matchScore = matchScore; }
    public MatchClassification getClassification() { return classification; }
    public void setClassification(MatchClassification classification) { this.classification = classification; }
    public String getMatchReasons() { return matchReasons; }
    public void setMatchReasons(String matchReasons) { this.matchReasons = matchReasons; }
    public MatchScoreBreakdownDto getScoreBreakdown() { return scoreBreakdown; }
    public void setScoreBreakdown(MatchScoreBreakdownDto scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; }
    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

}
