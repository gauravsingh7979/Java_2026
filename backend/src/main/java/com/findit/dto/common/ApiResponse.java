package com.findit.dto.common;


import java.time.LocalDateTime;

public class ApiResponse<T> {



    private boolean success = true;

    private String message;

    private T data;

    private LocalDateTime timestamp = LocalDateTime.now();


    // Default constructor
    public ApiResponse() {}

    // All-args constructor
    public ApiResponse(boolean success, String message, T data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }

    public static <T> Builder<T> builder() { return new Builder<T>(); }

    public static class Builder<T> {
        private boolean success = true;
        private String message;
        private T data;
        private LocalDateTime timestamp = LocalDateTime.now();
        public Builder<T> success(boolean success) { this.success = success; return this; }
        public Builder<T> message(String message) { this.message = message; return this; }
        public Builder<T> data(T data) { this.data = data; return this; }
        public Builder<T> timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }

        public ApiResponse<T> build() {
            return new ApiResponse<T>(success, message, data, timestamp);
        }
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    // Custom methods
    public static <T> ApiResponse<T> ok(T data, String message) {
            return ApiResponse.<T>builder()
                    .success(true)
                    .message(message)
                    .data(data)
                    .build();
        }
    
        public static <T> ApiResponse<T> ok(T data) {
            return ok(data, "Operation successful");
        }
    
        public static <T> ApiResponse<T> error(String message) {
            return ApiResponse.<T>builder()
                    .success(false)
                    .message(message)
                    .build();
        }

}
