package com.findit.dto.match;


import java.util.List;

public class MatchScoreBreakdownDto {



    private double totalScore;
    private double categoryScore;      // max 25
    private double titleSimilarityScore;// max 20
    private double brandScore;         // max 15
    private double colorScore;         // max 10
    private double locationScore;      // max 15
    private double dateProximityScore;  // max 10
    private double descriptionScore;   // max 5
    private List<String> matchReasons;


    // Default constructor
    public MatchScoreBreakdownDto() {}

    // All-args constructor
    public MatchScoreBreakdownDto(double totalScore, double categoryScore, double titleSimilarityScore, double brandScore, double colorScore, double locationScore, double dateProximityScore, double descriptionScore, List<String> matchReasons) {
        this.totalScore = totalScore;
        this.categoryScore = categoryScore;
        this.titleSimilarityScore = titleSimilarityScore;
        this.brandScore = brandScore;
        this.colorScore = colorScore;
        this.locationScore = locationScore;
        this.dateProximityScore = dateProximityScore;
        this.descriptionScore = descriptionScore;
        this.matchReasons = matchReasons;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private double totalScore;
        private double categoryScore;
        private double titleSimilarityScore;
        private double brandScore;
        private double colorScore;
        private double locationScore;
        private double dateProximityScore;
        private double descriptionScore;
        private List<String> matchReasons;
        public Builder totalScore(double totalScore) { this.totalScore = totalScore; return this; }
        public Builder categoryScore(double categoryScore) { this.categoryScore = categoryScore; return this; }
        public Builder titleSimilarityScore(double titleSimilarityScore) { this.titleSimilarityScore = titleSimilarityScore; return this; }
        public Builder brandScore(double brandScore) { this.brandScore = brandScore; return this; }
        public Builder colorScore(double colorScore) { this.colorScore = colorScore; return this; }
        public Builder locationScore(double locationScore) { this.locationScore = locationScore; return this; }
        public Builder dateProximityScore(double dateProximityScore) { this.dateProximityScore = dateProximityScore; return this; }
        public Builder descriptionScore(double descriptionScore) { this.descriptionScore = descriptionScore; return this; }
        public Builder matchReasons(List<String> matchReasons) { this.matchReasons = matchReasons; return this; }

        public MatchScoreBreakdownDto build() {
            return new MatchScoreBreakdownDto(totalScore, categoryScore, titleSimilarityScore, brandScore, colorScore, locationScore, dateProximityScore, descriptionScore, matchReasons);
        }
    }

    // Getters and Setters
    public double getTotalScore() { return totalScore; }
    public void setTotalScore(double totalScore) { this.totalScore = totalScore; }
    public double getCategoryScore() { return categoryScore; }
    public void setCategoryScore(double categoryScore) { this.categoryScore = categoryScore; }
    public double getTitleSimilarityScore() { return titleSimilarityScore; }
    public void setTitleSimilarityScore(double titleSimilarityScore) { this.titleSimilarityScore = titleSimilarityScore; }
    public double getBrandScore() { return brandScore; }
    public void setBrandScore(double brandScore) { this.brandScore = brandScore; }
    public double getColorScore() { return colorScore; }
    public void setColorScore(double colorScore) { this.colorScore = colorScore; }
    public double getLocationScore() { return locationScore; }
    public void setLocationScore(double locationScore) { this.locationScore = locationScore; }
    public double getDateProximityScore() { return dateProximityScore; }
    public void setDateProximityScore(double dateProximityScore) { this.dateProximityScore = dateProximityScore; }
    public double getDescriptionScore() { return descriptionScore; }
    public void setDescriptionScore(double descriptionScore) { this.descriptionScore = descriptionScore; }
    public List<String> getMatchReasons() { return matchReasons; }
    public void setMatchReasons(List<String> matchReasons) { this.matchReasons = matchReasons; }

}
