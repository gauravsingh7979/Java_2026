package com.findit.dto.admin;


import java.time.LocalDateTime;

public class AuditLogResponse {



    private Long id;
    private Long actorId;
    private String actorEmail;
    private String action;
    private String entityName;
    private Long entityId;
    private String details;
    private String ipAddress;
    private LocalDateTime createdAt;


    // Default constructor
    public AuditLogResponse() {}

    // All-args constructor
    public AuditLogResponse(Long id, Long actorId, String actorEmail, String action, String entityName, Long entityId, String details, String ipAddress, LocalDateTime createdAt) {
        this.id = id;
        this.actorId = actorId;
        this.actorEmail = actorEmail;
        this.action = action;
        this.entityName = entityName;
        this.entityId = entityId;
        this.details = details;
        this.ipAddress = ipAddress;
        this.createdAt = createdAt;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long actorId;
        private String actorEmail;
        private String action;
        private String entityName;
        private Long entityId;
        private String details;
        private String ipAddress;
        private LocalDateTime createdAt;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder actorId(Long actorId) { this.actorId = actorId; return this; }
        public Builder actorEmail(String actorEmail) { this.actorEmail = actorEmail; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder entityName(String entityName) { this.entityName = entityName; return this; }
        public Builder entityId(Long entityId) { this.entityId = entityId; return this; }
        public Builder details(String details) { this.details = details; return this; }
        public Builder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public AuditLogResponse build() {
            return new AuditLogResponse(id, actorId, actorEmail, action, entityName, entityId, details, ipAddress, createdAt);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }
    public String getActorEmail() { return actorEmail; }
    public void setActorEmail(String actorEmail) { this.actorEmail = actorEmail; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

}
