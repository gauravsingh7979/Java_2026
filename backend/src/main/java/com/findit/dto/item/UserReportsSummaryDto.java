package com.findit.dto.item;


import java.util.List;

public class UserReportsSummaryDto {



    private List<LostItemResponse> lostReports;
    private List<FoundItemResponse> foundReports;
    private int totalLost;
    private int totalFound;
    private int activeMatches;
    private int pendingClaims;


    // Default constructor
    public UserReportsSummaryDto() {}

    // All-args constructor
    public UserReportsSummaryDto(List<LostItemResponse> lostReports, List<FoundItemResponse> foundReports, int totalLost, int totalFound, int activeMatches, int pendingClaims) {
        this.lostReports = lostReports;
        this.foundReports = foundReports;
        this.totalLost = totalLost;
        this.totalFound = totalFound;
        this.activeMatches = activeMatches;
        this.pendingClaims = pendingClaims;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private List<LostItemResponse> lostReports;
        private List<FoundItemResponse> foundReports;
        private int totalLost;
        private int totalFound;
        private int activeMatches;
        private int pendingClaims;
        public Builder lostReports(List<LostItemResponse> lostReports) { this.lostReports = lostReports; return this; }
        public Builder foundReports(List<FoundItemResponse> foundReports) { this.foundReports = foundReports; return this; }
        public Builder totalLost(int totalLost) { this.totalLost = totalLost; return this; }
        public Builder totalFound(int totalFound) { this.totalFound = totalFound; return this; }
        public Builder activeMatches(int activeMatches) { this.activeMatches = activeMatches; return this; }
        public Builder pendingClaims(int pendingClaims) { this.pendingClaims = pendingClaims; return this; }

        public UserReportsSummaryDto build() {
            return new UserReportsSummaryDto(lostReports, foundReports, totalLost, totalFound, activeMatches, pendingClaims);
        }
    }

    // Getters and Setters
    public List<LostItemResponse> getLostReports() { return lostReports; }
    public void setLostReports(List<LostItemResponse> lostReports) { this.lostReports = lostReports; }
    public List<FoundItemResponse> getFoundReports() { return foundReports; }
    public void setFoundReports(List<FoundItemResponse> foundReports) { this.foundReports = foundReports; }
    public int getTotalLost() { return totalLost; }
    public void setTotalLost(int totalLost) { this.totalLost = totalLost; }
    public int getTotalFound() { return totalFound; }
    public void setTotalFound(int totalFound) { this.totalFound = totalFound; }
    public int getActiveMatches() { return activeMatches; }
    public void setActiveMatches(int activeMatches) { this.activeMatches = activeMatches; }
    public int getPendingClaims() { return pendingClaims; }
    public void setPendingClaims(int pendingClaims) { this.pendingClaims = pendingClaims; }

}
