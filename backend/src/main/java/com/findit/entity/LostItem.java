package com.findit.entity;

import com.findit.enums.ContactPreference;
import com.findit.enums.ItemCategory;
import com.findit.enums.ItemStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lost_items", indexes = {
    @Index(name = "idx_lost_items_tracking", columnList = "tracking_number", unique = true),
    @Index(name = "idx_lost_items_category", columnList = "category"),
    @Index(name = "idx_lost_items_status", columnList = "status"),
    @Index(name = "idx_lost_items_date", columnList = "date_lost"),
    @Index(name = "idx_lost_items_user", columnList = "user_id")
})
public class LostItem extends BaseEntity {

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

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(length = 100)
    private String brand;

    @Column(name = "primary_color", length = 50)
    private String primaryColor;

    @Column(name = "date_lost", nullable = false)
    private LocalDate dateLost;

    @Column(name = "location_lost", nullable = false, length = 200)
    private String locationLost;

    @Column(name = "distinctive_characteristics", columnDefinition = "TEXT")
    private String distinctiveCharacteristics;

    @Enumerated(EnumType.STRING)
    @Column(name = "contact_preference", nullable = false, length = 30)
    private ContactPreference contactPreference = ContactPreference.IN_APP;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemStatus status = ItemStatus.ACTIVE;

    @OneToMany(mappedBy = "lostItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "lostItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Match> matches = new ArrayList<>();

    public LostItem() {
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String trackingNumber;
        private User user;
        private String title;
        private ItemCategory category;
        private String description;
        private String brand;
        private String primaryColor;
        private LocalDate dateLost;
        private String locationLost;
        private String distinctiveCharacteristics;
        private ContactPreference contactPreference = ContactPreference.IN_APP;
        private ItemStatus status = ItemStatus.ACTIVE;

        public Builder trackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; return this; }
        public Builder user(User user) { this.user = user; return this; }
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

        public LostItem build() {
            LostItem item = new LostItem();
            item.setTrackingNumber(trackingNumber);
            item.setUser(user);
            item.setTitle(title);
            item.setCategory(category);
            item.setDescription(description);
            item.setBrand(brand);
            item.setPrimaryColor(primaryColor);
            item.setDateLost(dateLost);
            item.setLocationLost(locationLost);
            item.setDistinctiveCharacteristics(distinctiveCharacteristics);
            item.setContactPreference(contactPreference);
            item.setStatus(status);
            return item;
        }
    }

    public void addImage(ItemImage image) {
        images.add(image);
        image.setLostItem(this);
    }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
    public List<ItemImage> getImages() { return images; }
    public void setImages(List<ItemImage> images) { this.images = images; }
    public List<Match> getMatches() { return matches; }
    public void setMatches(List<Match> matches) { this.matches = matches; }
}
