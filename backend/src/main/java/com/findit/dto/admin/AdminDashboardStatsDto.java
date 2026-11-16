package com.findit.dto.admin;


import java.util.Map;

public class AdminDashboardStatsDto {



    private long totalUsers;
    private long activeUsers;
    private long totalLostReports;
    private long activeLostReports;
    private long totalFoundReports;
    private long activeFoundReports;
    private long totalMatches;
    private long strongMatches;
    private long pendingClaims;
    private long approvedClaims;
    private long totalRecoveries;
    private long completedRecoveries;
    private double recoveryRate;
    private Map<String, Long> reportsByCategory;


    // Default constructor
    public AdminDashboardStatsDto() {}

    // All-args constructor
    public AdminDashboardStatsDto(long totalUsers, long activeUsers, long totalLostReports, long activeLostReports, long totalFoundReports, long activeFoundReports, long totalMatches, long strongMatches, long pendingClaims, long approvedClaims, long totalRecoveries, long completedRecoveries, double recoveryRate, Map<String, Long> reportsByCategory) {
        this.totalUsers = totalUsers;
        this.activeUsers = activeUsers;
        this.totalLostReports = totalLostReports;
        this.activeLostReports = activeLostReports;
        this.totalFoundReports = totalFoundReports;
        this.activeFoundReports = activeFoundReports;
        this.totalMatches = totalMatches;
        this.strongMatches = strongMatches;
        this.pendingClaims = pendingClaims;
        this.approvedClaims = approvedClaims;
        this.totalRecoveries = totalRecoveries;
        this.completedRecoveries = completedRecoveries;
        this.recoveryRate = recoveryRate;
        this.reportsByCategory = reportsByCategory;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalUsers;
        private long activeUsers;
        private long totalLostReports;
        private long activeLostReports;
        private long totalFoundReports;
        private long activeFoundReports;
        private long totalMatches;
        private long strongMatches;
        private long pendingClaims;
        private long approvedClaims;
        private long totalRecoveries;
        private long completedRecoveries;
        private double recoveryRate;
        private Map<String, Long> reportsByCategory;
        public Builder totalUsers(long totalUsers) { this.totalUsers = totalUsers; return this; }
        public Builder activeUsers(long activeUsers) { this.activeUsers = activeUsers; return this; }
        public Builder totalLostReports(long totalLostReports) { this.totalLostReports = totalLostReports; return this; }
        public Builder activeLostReports(long activeLostReports) { this.activeLostReports = activeLostReports; return this; }
        public Builder totalFoundReports(long totalFoundReports) { this.totalFoundReports = totalFoundReports; return this; }
        public Builder activeFoundReports(long activeFoundReports) { this.activeFoundReports = activeFoundReports; return this; }
        public Builder totalMatches(long totalMatches) { this.totalMatches = totalMatches; return this; }
        public Builder strongMatches(long strongMatches) { this.strongMatches = strongMatches; return this; }
        public Builder pendingClaims(long pendingClaims) { this.pendingClaims = pendingClaims; return this; }
        public Builder approvedClaims(long approvedClaims) { this.approvedClaims = approvedClaims; return this; }
        public Builder totalRecoveries(long totalRecoveries) { this.totalRecoveries = totalRecoveries; return this; }
        public Builder completedRecoveries(long completedRecoveries) { this.completedRecoveries = completedRecoveries; return this; }
        public Builder recoveryRate(double recoveryRate) { this.recoveryRate = recoveryRate; return this; }
        public Builder reportsByCategory(Map<String, Long> reportsByCategory) { this.reportsByCategory = reportsByCategory; return this; }

        public AdminDashboardStatsDto build() {
            return new AdminDashboardStatsDto(totalUsers, activeUsers, totalLostReports, activeLostReports, totalFoundReports, activeFoundReports, totalMatches, strongMatches, pendingClaims, approvedClaims, totalRecoveries, completedRecoveries, recoveryRate, reportsByCategory);
        }
    }

    // Getters and Setters
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getActiveUsers() { return activeUsers; }
    public void setActiveUsers(long activeUsers) { this.activeUsers = activeUsers; }
    public long getTotalLostReports() { return totalLostReports; }
    public void setTotalLostReports(long totalLostReports) { this.totalLostReports = totalLostReports; }
    public long getActiveLostReports() { return activeLostReports; }
    public void setActiveLostReports(long activeLostReports) { this.activeLostReports = activeLostReports; }
    public long getTotalFoundReports() { return totalFoundReports; }
    public void setTotalFoundReports(long totalFoundReports) { this.totalFoundReports = totalFoundReports; }
    public long getActiveFoundReports() { return activeFoundReports; }
    public void setActiveFoundReports(long activeFoundReports) { this.activeFoundReports = activeFoundReports; }
    public long getTotalMatches() { return totalMatches; }
    public void setTotalMatches(long totalMatches) { this.totalMatches = totalMatches; }
    public long getStrongMatches() { return strongMatches; }
    public void setStrongMatches(long strongMatches) { this.strongMatches = strongMatches; }
    public long getPendingClaims() { return pendingClaims; }
    public void setPendingClaims(long pendingClaims) { this.pendingClaims = pendingClaims; }
    public long getApprovedClaims() { return approvedClaims; }
    public void setApprovedClaims(long approvedClaims) { this.approvedClaims = approvedClaims; }
    public long getTotalRecoveries() { return totalRecoveries; }
    public void setTotalRecoveries(long totalRecoveries) { this.totalRecoveries = totalRecoveries; }
    public long getCompletedRecoveries() { return completedRecoveries; }
    public void setCompletedRecoveries(long completedRecoveries) { this.completedRecoveries = completedRecoveries; }
    public double getRecoveryRate() { return recoveryRate; }
    public void setRecoveryRate(double recoveryRate) { this.recoveryRate = recoveryRate; }
    public Map<String, Long> getReportsByCategory() { return reportsByCategory; }
    public void setReportsByCategory(Map<String, Long> reportsByCategory) { this.reportsByCategory = reportsByCategory; }

}
