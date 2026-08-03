package com.kiosk.signature.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Captured signature data payload")
public class SignatureDataDto {

    @Schema(description = "Ready-to-use HTML/CSS Data URL image format", example = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD...")
    private String signatureDataUrl;

    @Schema(description = "Raw Base64-encoded JPEG image string", example = "/9j/4AAQSkZJRgABAQEASABIAAD...")
    private String imageBase64;

    @Schema(description = "Size of the JPEG signature image in bytes", example = "24580")
    private int imageSizeBytes;

    @Schema(description = "Length of vector stroke/raw biometric data in bytes", example = "350")
    private int strokeDataLength;

    @Schema(description = "Image format MIME type / extension", example = "jpeg")
    private String imageFormat;

    @Schema(description = "File path on disk if saved locally", example = "C:/temp/signatures/signature_20260802_165500.jpg")
    private String savedFilePath;

    @Schema(description = "Time taken to capture the signature in milliseconds", example = "4250")
    private long durationMs;

    @Schema(description = "Capture timestamp")
    private LocalDateTime captureTime;

    public SignatureDataDto() {
    }

    public SignatureDataDto(String signatureDataUrl, String imageBase64, int imageSizeBytes, int strokeDataLength,
                            String imageFormat, String savedFilePath, long durationMs, LocalDateTime captureTime) {
        this.signatureDataUrl = signatureDataUrl;
        this.imageBase64 = imageBase64;
        this.imageSizeBytes = imageSizeBytes;
        this.strokeDataLength = strokeDataLength;
        this.imageFormat = imageFormat;
        this.savedFilePath = savedFilePath;
        this.durationMs = durationMs;
        this.captureTime = captureTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String signatureDataUrl;
        private String imageBase64;
        private int imageSizeBytes;
        private int strokeDataLength;
        private String imageFormat = "jpeg";
        private String savedFilePath;
        private long durationMs;
        private LocalDateTime captureTime = LocalDateTime.now();

        public Builder signatureDataUrl(String signatureDataUrl) {
            this.signatureDataUrl = signatureDataUrl;
            return this;
        }

        public Builder imageBase64(String imageBase64) {
            this.imageBase64 = imageBase64;
            return this;
        }

        public Builder imageSizeBytes(int imageSizeBytes) {
            this.imageSizeBytes = imageSizeBytes;
            return this;
        }

        public Builder strokeDataLength(int strokeDataLength) {
            this.strokeDataLength = strokeDataLength;
            return this;
        }

        public Builder imageFormat(String imageFormat) {
            this.imageFormat = imageFormat;
            return this;
        }

        public Builder savedFilePath(String savedFilePath) {
            this.savedFilePath = savedFilePath;
            return this;
        }

        public Builder durationMs(long durationMs) {
            this.durationMs = durationMs;
            return this;
        }

        public Builder captureTime(LocalDateTime captureTime) {
            this.captureTime = captureTime;
            return this;
        }

        public SignatureDataDto build() {
            return new SignatureDataDto(signatureDataUrl, imageBase64, imageSizeBytes, strokeDataLength,
                    imageFormat, savedFilePath, durationMs, captureTime);
        }
    }

    public String getSignatureDataUrl() {
        return signatureDataUrl;
    }

    public void setSignatureDataUrl(String signatureDataUrl) {
        this.signatureDataUrl = signatureDataUrl;
    }

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public int getImageSizeBytes() {
        return imageSizeBytes;
    }

    public void setImageSizeBytes(int imageSizeBytes) {
        this.imageSizeBytes = imageSizeBytes;
    }

    public int getStrokeDataLength() {
        return strokeDataLength;
    }

    public void setStrokeDataLength(int strokeDataLength) {
        this.strokeDataLength = strokeDataLength;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public String getSavedFilePath() {
        return savedFilePath;
    }

    public void setSavedFilePath(String savedFilePath) {
        this.savedFilePath = savedFilePath;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public LocalDateTime getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(LocalDateTime captureTime) {
        this.captureTime = captureTime;
    }
}
