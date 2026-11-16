package com.findit.dto.notification;


import java.time.LocalDateTime;

public class NotificationResponse {



    private Long id;
    private String title;
    private String message;
    private String type;
    private String targetUrl;
    private boolean read;
    private LocalDateTime createdAt;


    // Default constructor
    public NotificationResponse() {}

    // All-args constructor
    public NotificationResponse(Long id, String title, String message, String type, String targetUrl, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.targetUrl = targetUrl;
        this.read = read;
        this.createdAt = createdAt;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String title;
        private String message;
        private String type;
        private String targetUrl;
        private boolean read;
        private LocalDateTime createdAt;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder targetUrl(String targetUrl) { this.targetUrl = targetUrl; return this; }
        public Builder read(boolean read) { this.read = read; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public NotificationResponse build() {
            return new NotificationResponse(id, title, message, type, targetUrl, read, createdAt);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

}
