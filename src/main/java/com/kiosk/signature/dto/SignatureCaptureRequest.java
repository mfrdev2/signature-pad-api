package com.kiosk.signature.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Parameters for capturing signature from FP430S pad")
public class SignatureCaptureRequest {

    @Schema(description = "Timeout in milliseconds (e.g. 60000). If not provided, defaults to configured default.", example = "60000")
    private Integer timeoutMs;

    @Schema(description = "Timeout in seconds (alternative to timeoutMs). E.g. 30", example = "30")
    private Integer timeoutSeconds;

    @Schema(description = "Encryption type (0: None/Default, 1: DES, 2: AES)", example = "0")
    private Integer encryptType;

    @Schema(description = "Whether to save captured signature image file locally on server", example = "false")
    private Boolean saveToDisk;

    @Schema(description = "Custom filename to save when saveToDisk is enabled (e.g. 'signature_user123.jpg')", example = "user_signature.jpg")
    private String filename;

    public SignatureCaptureRequest() {
    }

    public SignatureCaptureRequest(Integer timeoutMs, Integer timeoutSeconds, Integer encryptType, Boolean saveToDisk, String filename) {
        this.timeoutMs = timeoutMs;
        this.timeoutSeconds = timeoutSeconds;
        this.encryptType = encryptType;
        this.saveToDisk = saveToDisk;
        this.filename = filename;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Integer timeoutMs;
        private Integer timeoutSeconds;
        private Integer encryptType;
        private Boolean saveToDisk;
        private String filename;

        public Builder timeoutMs(Integer timeoutMs) {
            this.timeoutMs = timeoutMs;
            return this;
        }

        public Builder timeoutSeconds(Integer timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public Builder encryptType(Integer encryptType) {
            this.encryptType = encryptType;
            return this;
        }

        public Builder saveToDisk(Boolean saveToDisk) {
            this.saveToDisk = saveToDisk;
            return this;
        }

        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public SignatureCaptureRequest build() {
            return new SignatureCaptureRequest(timeoutMs, timeoutSeconds, encryptType, saveToDisk, filename);
        }
    }

    public Integer getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public Integer getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(Integer encryptType) {
        this.encryptType = encryptType;
    }

    public Boolean getSaveToDisk() {
        return saveToDisk;
    }

    public void setSaveToDisk(Boolean saveToDisk) {
        this.saveToDisk = saveToDisk;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "SignatureCaptureRequest{" +
                "timeoutMs=" + timeoutMs +
                ", timeoutSeconds=" + timeoutSeconds +
                ", encryptType=" + encryptType +
                ", saveToDisk=" + saveToDisk +
                ", filename='" + filename + '\'' +
                '}';
    }
}
