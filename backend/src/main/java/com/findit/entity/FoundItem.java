package com.findit.entity;

import com.findit.enums.ItemCategory;
import com.findit.enums.ItemStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "found_items", indexes = {
    @Index(name = "idx_found_items_tracking", columnList = "tracking_number", unique = true),
    @Index(name = "idx_found_items_category", columnList = "category"),
    @Index(name = "idx_found_items_status", columnList = "status"),
    @Index(name = "idx_found_items_date", columnList = "date_found"),
    @Index(name = "idx_found_items_user", columnList = "user_id")
})
public class FoundItem extends BaseEntity {

    @Column(name = "tracking_number", nullable = false, unique = true, length = 40)
    private String trackingNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ItemCategory category;

    @Column(name = "public_description", columnDefinition = "TEXT", nullable = false)
    private String publicDescription;

    @Column(name = "private_details", columnDefinition = "TEXT")
    private String privateDetails;

    @Column(length = 100)
    private String brand;

    @Column(name = "primary_color", length = 50)
    private String primaryColor;

    @Column(name = "date_found", nullable = false)
    private LocalDate dateFound;

    @Column(name = "location_found", nullable = false, length = 200)
    private String locationFound;

    @Column(name = "storage_location", length = 200)
    private String storageLocation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemStatus status = ItemStatus.ACTIVE;

    @OneToMany(mappedBy = "foundItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "foundItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Match> matches = new ArrayList<>();

    @OneToMany(mappedBy = "foundItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Claim> claims = new ArrayList<>();

    public FoundItem() {
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String trackingNumber;
        private User user;
        private String title;
        private ItemCategory category;
        private String publicDescription;
        private String privateDetails;
        private String brand;
        private String primaryColor;
        private LocalDate dateFound;
        private String locationFound;
        private String storageLocation;
        private ItemStatus status = ItemStatus.ACTIVE;

        public Builder trackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; return this; }
        public Builder user(User user) { this.user = user; return this; }
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

        public FoundItem build() {
            FoundItem item = new FoundItem();
            item.setTrackingNumber(trackingNumber);
            item.setUser(user);
            item.setTitle(title);
            item.setCategory(category);
            item.setPublicDescription(publicDescription);
            item.setPrivateDetails(privateDetails);
            item.setBrand(brand);
            item.setPrimaryColor(primaryColor);
            item.setDateFound(dateFound);
            item.setLocationFound(locationFound);
            item.setStorageLocation(storageLocation);
            item.setStatus(status);
            return item;
        }
    }

    public void addImage(ItemImage image) {
        images.add(image);
        image.setFoundItem(this);
    }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
    public List<ItemImage> getImages() { return images; }
    public void setImages(List<ItemImage> images) { this.images = images; }
    public List<Match> getMatches() { return matches; }
    public void setMatches(List<Match> matches) { this.matches = matches; }
    public List<Claim> getClaims() { return claims; }
    public void setClaims(List<Claim> claims) { this.claims = claims; }
}
