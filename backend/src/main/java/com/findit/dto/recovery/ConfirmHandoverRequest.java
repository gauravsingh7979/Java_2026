package com.findit.dto.recovery;

import jakarta.validation.constraints.NotBlank;

public class ConfirmHandoverRequest {



    @NotBlank(message = "Recovery reference code is required to verify handover")
    private String recoveryCode;

    private String handoverNotes;


    // Default constructor
    public ConfirmHandoverRequest() {}

    // All-args constructor
    public ConfirmHandoverRequest(String recoveryCode, String handoverNotes) {
        this.recoveryCode = recoveryCode;
        this.handoverNotes = handoverNotes;
    }

    public static  Builder builder() { return new Builder(); }

    public static class Builder {
        private String recoveryCode;
        private String handoverNotes;
        public Builder recoveryCode(String recoveryCode) { this.recoveryCode = recoveryCode; return this; }
        public Builder handoverNotes(String handoverNotes) { this.handoverNotes = handoverNotes; return this; }

        public ConfirmHandoverRequest build() {
            return new ConfirmHandoverRequest(recoveryCode, handoverNotes);
        }
    }

    // Getters and Setters
    public String getRecoveryCode() { return recoveryCode; }
    public void setRecoveryCode(String recoveryCode) { this.recoveryCode = recoveryCode; }
    public String getHandoverNotes() { return handoverNotes; }
    public void setHandoverNotes(String handoverNotes) { this.handoverNotes = handoverNotes; }

}
