package com.findit.matching;

import com.findit.dto.match.MatchScoreBreakdownDto;
import com.findit.enums.MatchClassification;

import java.util.List;

public class MatchScoringResult {

    private double totalScore;
    private MatchClassification classification;
    private MatchScoreBreakdownDto scoreBreakdown;
    private List<String> reasons;
    private String summaryReason;

    public MatchScoringResult() {
    }

    public MatchScoringResult(double totalScore, MatchClassification classification, MatchScoreBreakdownDto scoreBreakdown, List<String> reasons, String summaryReason) {
        this.totalScore = totalScore;
        this.classification = classification;
        this.scoreBreakdown = scoreBreakdown;
        this.reasons = reasons;
        this.summaryReason = summaryReason;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private double totalScore;
        private MatchClassification classification;
        private MatchScoreBreakdownDto scoreBreakdown;
        private List<String> reasons;
        private String summaryReason;

        public Builder totalScore(double totalScore) { this.totalScore = totalScore; return this; }
        public Builder classification(MatchClassification classification) { this.classification = classification; return this; }
        public Builder scoreBreakdown(MatchScoreBreakdownDto scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; return this; }
        public Builder reasons(List<String> reasons) { this.reasons = reasons; return this; }
        public Builder summaryReason(String summaryReason) { this.summaryReason = summaryReason; return this; }

        public MatchScoringResult build() {
            return new MatchScoringResult(totalScore, classification, scoreBreakdown, reasons, summaryReason);
        }
    }

    public static MatchClassification classify(double score) {
        if (score >= 80.0) {
            return MatchClassification.STRONG;
        } else if (score >= 60.0) {
            return MatchClassification.POSSIBLE;
        } else {
            return MatchClassification.WEAK;
        }
    }

    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }
    public MatchClassification getClassification() { return classification; }
    public void setClassification(MatchClassification classification) { this.classification = classification; }
    public MatchScoreBreakdownDto getScoreBreakdown() { return scoreBreakdown; }
    public void setScoreBreakdown(MatchScoreBreakdownDto scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; }
    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }
    public String getSummaryReason() { return summaryReason; }
    public void setSummaryReason(String summaryReason) { this.summaryReason = summaryReason; }
}
