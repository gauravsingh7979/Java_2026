package com.findit.dto.item;

import com.findit.enums.ItemCategory;
import com.findit.enums.ItemStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FoundItemResponse {



    private Long id;
    private String trackingNumber;
    private Long userId;
    private String finderName;
    private String finderEmail;
    private String title;
    private ItemCategory category;
    private String publicDescription;

    // Secure field: null unless requester is finder or admin
    private String privateDetails;

    private String brand;
    private String primaryColor;
    private LocalDate dateFound;
    private String locationFound;
    private String storageLocation;
    private ItemStatus status;
    private List<ItemImageResponse> images;
    private int matchCount;
    private int claimCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // Default constructor
    public FoundItemResponse() {}

    // All-args constructor
    public FoundItemResponse(Long id, String trackingNumber, Long userId, String finderName, String finderEmail, String title, ItemCategory category, String publicDescription, String privateDetails, String brand, String primaryColor, LocalDate dateFound, String locationFound, String storageLocation, ItemStatus status, List<ItemImageResponse> images, int matchCount, int claimCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.trackingNumber = trackingNumber;
        this.userId = userId;
        this.finderName = finderName;
        this.finderEmail = finderEmail;
        this.title = title;
        this.category = category;
        this.publicDescription = publicDescription;
        this.privateDetails = privateDetails;
        this.brand = brand;
        this.primaryColor = primaryColor;
        this.dateFound = dateFound;
        this.locationFound = locationFound;
        this.storageLocation = storageLocation;
        this.status = status;
        this.images = images;
        this.matchCount = matchCount;
        this.claimCount = claimCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String trackingNumber;
        private Long userId;
        private String finderName;
        private String finderEmail;
        private String title;
        private ItemCategory category;
        private String publicDescription;
        private String privateDetails;
        private String brand;
        private String primaryColor;
        private LocalDate dateFound;
        private String locationFound;
        private String storageLocation;
        private ItemStatus status;
        private List<ItemImageResponse> images;
        private int matchCount;
        private int claimCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder trackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder finderName(String finderName) { this.finderName = finderName; return this; }
        public Builder finderEmail(String finderEmail) { this.finderEmail = finderEmail; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder category(ItemCategory category) { this.category = category; return this; }
        public Builder publicDescription(String publicDescription) { this.publicDescription = publicDescription; return this; }
        public Builder privateDetails(String privateDetails) { this.privateDetails = privateDetails; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder primaryColor(String primaryColor) { this.primaryColor = primaryColor; return this; }
        public Builder dateFound(LocalDate dateFound) { this.dateFound = dateFound; return this; }
        public Builder locationFound(String locationFound) { this.locationFound = locationFound; return this; }
        public Builder storageLocation(String storageLocation) { this.storageLocation = storageLocation; return this; }
        public Builder status(ItemStatus status) { this.status = status; return this; }
        public Builder images(List<ItemImageResponse> images) { this.images = images; return this; }
        public Builder matchCount(int matchCount) { this.matchCount = matchCount; return this; }
        public Builder claimCount(int claimCount) { this.claimCount = claimCount; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public FoundItemResponse build() {
            return new FoundItemResponse(id, trackingNumber, userId, finderName, finderEmail, title, category, publicDescription, privateDetails, brand, primaryColor, dateFound, locationFound, storageLocation, status, images, matchCount, claimCount, createdAt, updatedAt);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFinderName() { return finderName; }
    public void setFinderName(String finderName) { this.finderName = finderName; }
    public String getFinderEmail() { return finderEmail; }
    public void setFinderEmail(String finderEmail) { this.finderEmail = finderEmail; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public ItemCategory getCategory() { return category; }
    public void setCategory(ItemCategory category) { this.category = category; }
    public String getPublicDescription() { return publicDescription; }
    public void setPublicDescription(String publicDescription) { this.publicDescription = publicDescription; }
    public String getPrivateDetails() { return privateDetails; }
    public void setPrivateDetails(String privateDetails) { this.privateDetails = privateDetails; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getPrimaryColor() { return primaryColor; }
    public void setPrimaryColor(String primaryColor) { this.primaryColor = primaryColor; }
    public LocalDate getDateFound() { return dateFound; }
    public void setDateFound(LocalDate dateFound) { this.dateFound = dateFound; }
    public String getLocationFound() { return locationFound; }
    public void setLocationFound(String locationFound) { this.locationFound = locationFound; }
    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }
    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }
    public List<ItemImageResponse> getImages() { return images; }
    public void setImages(List<ItemImageResponse> images) { this.images = images; }
    public int getMatchCount() { return matchCount; }
    public void setMatchCount(int matchCount) { this.matchCount = matchCount; }
    public int getClaimCount() { return claimCount; }
    public void setClaimCount(int claimCount) { this.claimCount = claimCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
