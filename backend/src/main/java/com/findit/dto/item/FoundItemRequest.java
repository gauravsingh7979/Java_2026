package com.findit.dto.item;

import com.findit.enums.ItemCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class FoundItemRequest {



    @NotBlank(message = "Item name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String title;

    @NotNull(message = "Category is required")
    private ItemCategory category;

    @NotBlank(message = "Public description is required")
    private String publicDescription;

    // Private clues (serials, card names, hidden marks) - not exposed publicly
    private String privateDetails;

    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String primaryColor;

    @NotNull(message = "Date found is required")
    @PastOrPresent(message = "Date found cannot be in the future")
    private LocalDate dateFound;

    @NotBlank(message = "Location found is required")
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String locationFound;

    @Size(max = 200, message = "Storage location must not exceed 200 characters")
    private String storageLocation;


    // Default constructor
    public FoundItemRequest() {}

    // All-args constructor
    public FoundItemRequest(String title, ItemCategory category, String publicDescription, String privateDetails, String brand, String primaryColor, LocalDate dateFound, String locationFound, String storageLocation) {
        this.title = title;
        this.category = category;
        this.publicDescription = publicDescription;
        this.privateDetails = privateDetails;
        this.brand = brand;
        this.primaryColor = primaryColor;
        this.dateFound = dateFound;
        this.locationFound = locationFound;
        this.storageLocation = storageLocation;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private String title;
        private ItemCategory category;
        private String publicDescription;
        private String privateDetails;
        private String brand;
        private String primaryColor;
        private LocalDate dateFound;
        private String locationFound;
        private String storageLocation;
        public Builder title(String title) { this.title = title; return this; }
        public Builder category(ItemCategory category) { this.category = category; return this; }
        public Builder publicDescription(String publicDescription) { this.publicDescription = publicDescription; return this; }
        public Builder privateDetails(String privateDetails) { this.privateDetails = privateDetails; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder primaryColor(String primaryColor) { this.primaryColor = primaryColor; return this; }
        public Builder dateFound(LocalDate dateFound) { this.dateFound = dateFound; return this; }
        public Builder locationFound(String locationFound) { this.locationFound = locationFound; return this; }
        public Builder storageLocation(String storageLocation) { this.storageLocation = storageLocation; return this; }

        public FoundItemRequest build() {
            return new FoundItemRequest(title, category, publicDescription, privateDetails, brand, primaryColor, dateFound, locationFound, storageLocation);
        }
    }

    // Getters and Setters
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

}
