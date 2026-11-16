package com.findit.dto.claim;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClaimRequest {



    @NotNull(message = "Found item ID is required")
    private Long foundItemId;

    @NotBlank(message = "Explanation of ownership is required")
    private String ownershipExplanation;

    @NotBlank(message = "Distinctive characteristics/private details are required for verification")
    private String distinctiveCharacteristics;

    private String supportingDocumentUrl;


    // Default constructor
    public ClaimRequest() {}

    // All-args constructor
    public ClaimRequest(Long foundItemId, String ownershipExplanation, String distinctiveCharacteristics, String supportingDocumentUrl) {
        this.foundItemId = foundItemId;
        this.ownershipExplanation = ownershipExplanation;
        this.distinctiveCharacteristics = distinctiveCharacteristics;
        this.supportingDocumentUrl = supportingDocumentUrl;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private Long foundItemId;
        private String ownershipExplanation;
        private String distinctiveCharacteristics;
        private String supportingDocumentUrl;
        public Builder foundItemId(Long foundItemId) { this.foundItemId = foundItemId; return this; }
        public Builder ownershipExplanation(String ownershipExplanation) { this.ownershipExplanation = ownershipExplanation; return this; }
        public Builder distinctiveCharacteristics(String distinctiveCharacteristics) { this.distinctiveCharacteristics = distinctiveCharacteristics; return this; }
        public Builder supportingDocumentUrl(String supportingDocumentUrl) { this.supportingDocumentUrl = supportingDocumentUrl; return this; }

        public ClaimRequest build() {
            return new ClaimRequest(foundItemId, ownershipExplanation, distinctiveCharacteristics, supportingDocumentUrl);
        }
    }

    // Getters and Setters
    public Long getFoundItemId() { return foundItemId; }
    public void setFoundItemId(Long foundItemId) { this.foundItemId = foundItemId; }
    public String getOwnershipExplanation() { return ownershipExplanation; }
    public void setOwnershipExplanation(String ownershipExplanation) { this.ownershipExplanation = ownershipExplanation; }
    public String getDistinctiveCharacteristics() { return distinctiveCharacteristics; }
    public void setDistinctiveCharacteristics(String distinctiveCharacteristics) { this.distinctiveCharacteristics = distinctiveCharacteristics; }
    public String getSupportingDocumentUrl() { return supportingDocumentUrl; }
    public void setSupportingDocumentUrl(String supportingDocumentUrl) { this.supportingDocumentUrl = supportingDocumentUrl; }

}
