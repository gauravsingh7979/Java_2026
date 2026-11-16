package com.findit.dto.user;

import com.findit.enums.Role;

import java.time.LocalDateTime;

public class UserProfileResponse {



    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private Role role;
    private boolean active;
    private boolean verified;
    private LocalDateTime createdAt;
    private long totalLostReports;
    private long totalFoundReports;
    private long totalClaims;


    // Default constructor
    public UserProfileResponse() {}

    // All-args constructor
    public UserProfileResponse(Long id, String email, String firstName, String lastName, String phoneNumber, Role role, boolean active, boolean verified, LocalDateTime createdAt, long totalLostReports, long totalFoundReports, long totalClaims) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.active = active;
        this.verified = verified;
        this.createdAt = createdAt;
        this.totalLostReports = totalLostReports;
        this.totalFoundReports = totalFoundReports;
        this.totalClaims = totalClaims;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private Role role;
        private boolean active;
        private boolean verified;
        private LocalDateTime createdAt;
        private long totalLostReports;
        private long totalFoundReports;
        private long totalClaims;
        public Builder id(Long id) { this.id = id; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder role(Role role) { this.role = role; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder verified(boolean verified) { this.verified = verified; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder totalLostReports(long totalLostReports) { this.totalLostReports = totalLostReports; return this; }
        public Builder totalFoundReports(long totalFoundReports) { this.totalFoundReports = totalFoundReports; return this; }
        public Builder totalClaims(long totalClaims) { this.totalClaims = totalClaims; return this; }

        public UserProfileResponse build() {
            return new UserProfileResponse(id, email, firstName, lastName, phoneNumber, role, active, verified, createdAt, totalLostReports, totalFoundReports, totalClaims);
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public long getTotalLostReports() { return totalLostReports; }
    public void setTotalLostReports(long totalLostReports) { this.totalLostReports = totalLostReports; }
    public long getTotalFoundReports() { return totalFoundReports; }
    public void setTotalFoundReports(long totalFoundReports) { this.totalFoundReports = totalFoundReports; }
    public long getTotalClaims() { return totalClaims; }
    public void setTotalClaims(long totalClaims) { this.totalClaims = totalClaims; }

}
