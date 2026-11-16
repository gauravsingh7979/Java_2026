package com.findit.dto.item;

import com.findit.enums.ContactPreference;
import com.findit.enums.ItemCategory;
import com.findit.enums.ItemStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LostItemResponse {



    private Long id;
    private String trackingNumber;
    private Long userId;
    private String reporterName;
    private String reporterEmail;
    private String title;
    private ItemCategory category;
    private String description;
    private String brand;
    private String primaryColor;
    private LocalDate dateLost;
    private String locationLost;
    private String distinctiveCharacteristics;
    private ContactPreference contactPreference;
    private ItemStatus status;
    private List<ItemImageResponse> images;
    private int matchCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // Default constructor
    public LostItemResponse() {}

    // All-args constructor
    public LostItemResponse(Long id, String trackingNumber, Long userId, String reporterName, String reporterEmail, String title, ItemCategory category, String description, String brand, String primaryColor, LocalDate dateLost, String locationLost, String distinctiveCharacteristics, ContactPreference contactPreference, ItemStatus status, List<ItemImageResponse> images, int matchCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.trackingNumber = trackingNumber;
        this.userId = userId;
        this.reporterName = reporterName;
        this.reporterEmail = reporterEmail;
        this.title = title;
        this.category = category;
        this.description = description;
        this.brand = brand;
        this.primaryColor = primaryColor;
        this.dateLost = dateLost;
        this.locationLost = locationLost;
        this.distinctiveCharacteristics = distinctiveCharacteristics;
        this.contactPreference = contactPreference;
        this.status = status;
        this.images = images;
        this.matchCount = matchCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String trackingNumber;
        private Long userId;
        private String reporterName;
        private String reporterEmail;
        private String title;
        private ItemCategory category;
        private String description;
        private String brand;
        private String primaryColor;
        private LocalDate dateLost;
        private String locationLost;
        private String distinctiveCharacteristics;
        private ContactPreference contactPreference;
        private ItemStatus status;
        private List<ItemImageResponse> images;
        private int matchCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder trackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder reporterName(String reporterName) { this.reporterName = reporterName; return this; }
        public Builder reporterEmail(String reporterEmail) { this.reporterEmail = reporterEmail; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder category(ItemCategory category) { this.category = category; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder primaryColor(String primaryColor) { this.primaryColor = primaryColor; return this; }
        public Builder dateLost(LocalDate dateLost) { this.dateLost = dateLost; return this; }
        public Builder locationLost(String locationLost) { this.locationLost = locationLost; return this; }
        public Builder distinctiveCharacteristics(String distinctiveCharacteristics) { this.distinctiveCharacteristics = distinctiveCharacteristics; return this; }
        public Builder contactPreference(ContactPreference contactPreference) { this.contactPreference = contactPreference; return this; }
        public Builder status(ItemStatus status) { this.status = status; return this; }
        public Builder images(List<ItemImageResponse> images) { this.images = images; return this; }
        public Builder matchCount(int matchCount) { this.matchCount = matchCount; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public LostItemResponse build() {
            return new LostItemResponse(id, trackingNumber, userId, reporterName, reporterEmail, title, category, description, brand, primaryColor, dateLost, locationLost, distinctiveCharacteristics, contactPreference, status, images, matchCount, createdAt, updatedAt);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
    public String getReporterEmail() { return reporterEmail; }
    public void setReporterEmail(String reporterEmail) { this.reporterEmail = reporterEmail; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public ItemCategory getCategory() { return category; }
    public void setCategory(ItemCategory category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }
    public LocalDate getDateLost() { return dateLost; }
    public void setDateLost(LocalDate dateLost) { this.dateLost = dateLost; }
    public String getLocationLost() { return locationLost; }
    public void setLocationLost(String locationLost) { this.locationLost = locationLost; }
    public String getDistinctiveCharacteristics() { return distinctiveCharacteristics; }
    public void setDistinctiveCharacteristics(String distinctiveCharacteristics) { this.distinctiveCharacteristics = distinctiveCharacteristics; }
    public ContactPreference getContactPreference() { return contactPreference; }
    public void setContactPreference(ContactPreference contactPreference) { this.contactPreference = contactPreference; }
    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }
    public List<ItemImageResponse> getImages() { return images; }
    public void setImages(List<ItemImageResponse> images) { this.images = images; }
    public int getMatchCount() { return matchCount; }
    public void setMatchCount(int matchCount) { this.matchCount = matchCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
