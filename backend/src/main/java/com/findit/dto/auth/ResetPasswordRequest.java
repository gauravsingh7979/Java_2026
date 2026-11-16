package com.findit.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {



    @NotBlank(message = "Token is required")
    private String token;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    private String newPassword;


    // Default constructor
    public ResetPasswordRequest() {}

    // All-args constructor
    public ResetPasswordRequest(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private String token;
        private String newPassword;
        public Builder token(String token) { this.token = token; return this; }
        public Builder newPassword(String newPassword) { this.newPassword = newPassword; return this; }

        public ResetPasswordRequest build() {
            return new ResetPasswordRequest(token, newPassword);
        }
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }

}
