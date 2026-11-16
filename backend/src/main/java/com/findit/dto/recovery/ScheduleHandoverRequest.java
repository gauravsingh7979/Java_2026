package com.findit.dto.recovery;

import java.time.LocalDateTime;

public class ScheduleHandoverRequest {

    private LocalDateTime scheduledAt;
    private LocalDateTime handoverDate;
    private String handoverLocation;
    private String handoverNotes;
    private String notes;

    // Default constructor
    public ScheduleHandoverRequest() {}

    // All-args constructor
    public ScheduleHandoverRequest(LocalDateTime scheduledAt, LocalDateTime handoverDate, String handoverLocation, String handoverNotes, String notes) {
        this.scheduledAt = scheduledAt;
        this.handoverDate = handoverDate;
        this.handoverLocation = handoverLocation;
        this.handoverNotes = handoverNotes;
        this.notes = notes;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private LocalDateTime scheduledAt;
        private LocalDateTime handoverDate;
        private String handoverLocation;
        private String handoverNotes;
        private String notes;

        public Builder scheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; return this; }
        public Builder handoverDate(LocalDateTime handoverDate) { this.handoverDate = handoverDate; return this; }
        public Builder handoverLocation(String handoverLocation) { this.handoverLocation = handoverLocation; return this; }
        public Builder handoverNotes(String handoverNotes) { this.handoverNotes = handoverNotes; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }

        public ScheduleHandoverRequest build() {
            return new ScheduleHandoverRequest(scheduledAt, handoverDate, handoverLocation, handoverNotes, notes);
        }
    }

    public LocalDateTime getEffectiveScheduledAt() {
        return scheduledAt != null ? scheduledAt : handoverDate;
    }

    public String getEffectiveNotes() {
        StringBuilder sb = new StringBuilder();
        if (handoverLocation != null && !handoverLocation.isBlank()) {
            sb.append("Location: ").append(handoverLocation.trim());
        }
        String n = handoverNotes != null ? handoverNotes : notes;
        if (n != null && !n.isBlank()) {
            if (sb.length() > 0) sb.append(" | Notes: ");
            sb.append(n.trim());
        }
        return sb.toString();
    }

    // Getters and Setters
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    public LocalDateTime getHandoverDate() { return handoverDate; }
    public void setHandoverDate(LocalDateTime handoverDate) { this.handoverDate = handoverDate; }
    public String getHandoverLocation() { return handoverLocation; }
    public void setHandoverLocation(String handoverLocation) { this.handoverLocation = handoverLocation; }
    public String getHandoverNotes() { return handoverNotes; }
    public void setHandoverNotes(String handoverNotes) { this.handoverNotes = handoverNotes; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}