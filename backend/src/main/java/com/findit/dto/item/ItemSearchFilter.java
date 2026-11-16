package com.findit.dto.item;

import com.findit.enums.ItemCategory;
import com.findit.enums.ItemStatus;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class ItemSearchFilter {



    private String query;
    private ItemCategory category;
    private String location;
    private ItemStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private int page = 0;

    private int size = 12;

    private String sortBy = "createdAt";

    private String sortDirection = "DESC";


    // Default constructor
    public ItemSearchFilter() {}

    // All-args constructor
    public ItemSearchFilter(String query, ItemCategory category, String location, ItemStatus status, LocalDate startDate, LocalDate endDate, int page, int size, String sortBy, String sortDirection) {
        this.query = query;
        this.category = category;
        this.location = location;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private String query;
        private ItemCategory category;
        private String location;
        private ItemStatus status;
        private LocalDate startDate;
        private LocalDate endDate;
        private int page = 0;
        private int size = 12;
        private String sortBy = "createdAt";
        private String sortDirection = "DESC";
        public Builder query(String query) { this.query = query; return this; }
        public Builder category(ItemCategory category) { this.category = category; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder status(ItemStatus status) { this.status = status; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder page(int page) { this.page = page; return this; }
        public Builder size(int size) { this.size = size; return this; }
        public Builder sortBy(String sortBy) { this.sortBy = sortBy; return this; }
        public Builder sortDirection(String sortDirection) { this.sortDirection = sortDirection; return this; }

        public ItemSearchFilter build() {
            return new ItemSearchFilter(query, category, location, status, startDate, endDate, page, size, sortBy, sortDirection);
        }
    }

    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public ItemCategory getCategory() { return category; }
    public void setCategory(ItemCategory category) { this.category = category; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }

}
