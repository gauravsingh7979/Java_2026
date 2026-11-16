package com.findit.dto.item;

import com.findit.enums.ContactPreference;
import com.findit.enums.ItemCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class LostItemRequest {



    @NotBlank(message = "Item title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    @NotNull(message = "Category is required")
    private ItemCategory category;

    @NotBlank(message = "Description is required")
    private String description;

    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String primaryColor;

    @NotNull(message = "Date lost is required")
    @PastOrPresent(message = "Date lost cannot be in the future")
    private LocalDate dateLost;

    @NotBlank(message = "Location lost is required")
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String locationLost;

    private String distinctiveCharacteristics;

    @NotNull(message = "Contact preference is required")
    private ContactPreference contactPreference;


    // Default constructor
    public LostItemRequest() {}

    // All-args constructor
    public LostItemRequest(String title, ItemCategory category, String description, String brand, String primaryColor, LocalDate dateLost, String locationLost, String distinctiveCharacteristics, ContactPreference contactPreference) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.brand = brand;
        this.primaryColor = primaryColor;
        this.dateLost = dateLost;
        this.locationLost = locationLost;
        this.distinctiveCharacteristics = distinctiveCharacteristics;
        this.contactPreference = contactPreference;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private String title;
        private ItemCategory category;
        private String description;
        private String brand;
        private String primaryColor;
        private LocalDate dateLost;
        private String locationLost;
        private String distinctiveCharacteristics;
        private ContactPreference contactPreference;
        public Builder title(String title) { this.title = title; return this; }
        public Builder category(ItemCategory category) { this.category = category; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder primaryColor(String primaryColor) { this.primaryColor = primaryColor; return this; }
        public Builder dateLost(LocalDate dateLost) { this.dateLost = dateLost; return this; }
        public Builder locationLost(String locationLost) { this.locationLost = locationLost; return this; }
        public Builder distinctiveCharacteristics(String distinctiveCharacteristics) { this.distinctiveCharacteristics = distinctiveCharacteristics; return this; }
        public Builder contactPreference(ContactPreference contactPreference) { this.contactPreference = contactPreference; return this; }

        public LostItemRequest build() {
            return new LostItemRequest(title, category, description, brand, primaryColor, dateLost, locationLost, distinctiveCharacteristics, contactPreference);
        }
    }

    // Getters and Setters
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

}
