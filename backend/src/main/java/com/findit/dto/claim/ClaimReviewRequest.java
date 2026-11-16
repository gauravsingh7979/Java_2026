package com.findit.dto.claim;

import jakarta.validation.constraints.NotBlank;

public class ClaimReviewRequest {



    @NotBlank(message = "Decision explanation/reason is mandatory")
    private String decisionReason;


    // Default constructor
    public ClaimReviewRequest() {}

    // All-args constructor
    public ClaimReviewRequest(String decisionReason) {
        this.decisionReason = decisionReason;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private String decisionReason;
        public Builder decisionReason(String decisionReason) { this.decisionReason = decisionReason; return this; }

        public ClaimReviewRequest build() {
            return new ClaimReviewRequest(decisionReason);
        }
    }

    // Getters and Setters
    public String getDecisionReason() { return decisionReason; }
    public void setDecisionReason(String decisionReason) { this.decisionReason = decisionReason; }

}
