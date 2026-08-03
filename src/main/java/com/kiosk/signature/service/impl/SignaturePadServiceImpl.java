package com.kiosk.signature.service.impl;

import com.kiosk.signature.config.SignaturePadProperties;
import com.kiosk.signature.dto.SignatureCaptureRequest;
import com.kiosk.signature.dto.SignatureDataDto;
import com.kiosk.signature.exception.SignaturePadException;
import com.kiosk.signature.native_lib.FP430SLibrary;
import com.kiosk.signature.service.SignaturePadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SignaturePadServiceImpl implements SignaturePadService {

    private static final Logger log = LoggerFactory.getLogger(SignaturePadServiceImpl.class);
    private static final int BUFFER_SIZE = 1024 * 400; // 400KB buffer required by SDK
    private static final String PNG_DATA_URL_PREFIX = "data:image/png;base64,";

    private final FP430SLibrary fp430SLibrary;
    private final SignaturePadProperties properties;
    private final ReentrantLock hardwareLock = new ReentrantLock(true);

    public SignaturePadServiceImpl(FP430SLibrary fp430SLibrary, SignaturePadProperties properties) {
        this.fp430SLibrary = fp430SLibrary;
        this.properties = properties;
    }

    @Override
    public SignatureDataDto captureSignature(SignatureCaptureRequest request) {
        log.info("Signature capture request received: {}", request);
        long startTime = System.currentTimeMillis();

        int timeoutMs = resolveTimeout(request);
        int encryptType = (request != null && request.getEncryptType() != null)
                ? request.getEncryptType()
                : properties.getEncryptType();

        boolean saveToDisk = (request != null && request.getSaveToDisk() != null)
                ? request.getSaveToDisk()
                : properties.isSaveToDisk();

        String customFilename = (request != null) ? request.getFilename() : null;

        byte[] rawBuffer = new byte[BUFFER_SIZE];
        byte[] imageBytes;
        int len1;
        int len2;

        boolean lockAcquired = false;
        try {
            // Lock hardware so multiple concurrent HTTP requests don't conflict
            lockAcquired = hardwareLock.tryLock((timeoutMs / 1000L) + 5, TimeUnit.SECONDS);
            if (!lockAcquired) {
                throw new SignaturePadException("Signature pad is currently in use by another request. Please try again.",
                        409, HttpStatus.CONFLICT);
            }

            log.debug("Opening USB connection to FP430S device...");
            int openStatus = fp430SLibrary.USB_Open();
            log.info("FP430S USB_Open returned status: {}", openStatus);
            if (openStatus != 0) {
                throw new SignaturePadException("Failed to open FP430S USB signature pad (status code: " + openStatus + "). Please check USB connection.",
                        503, HttpStatus.SERVICE_UNAVAILABLE);
            }

            try {
                log.info("Prompting signature on FP430S pad (timeout: {} ms, encryptType: {})...", timeoutMs, encryptType);
                int captureResult = fp430SLibrary.Lib_GetSignature(encryptType, rawBuffer, timeoutMs);
                log.info("FP430S Lib_GetSignature returned code: {}", captureResult);

                if (captureResult != 0) {
                    throw new SignaturePadException("Signature capture cancelled or timed out (device error code: " + captureResult + ").",
                            408, HttpStatus.REQUEST_TIMEOUT);
                }

                // Decode stroke length (len1) and image length (len2) from header bytes (3 bytes each)
                len1 = byteToInt(rawBuffer[0]) * 65536 + byteToInt(rawBuffer[1]) * 256 + byteToInt(rawBuffer[2]);
                len2 = byteToInt(rawBuffer[3]) * 65536 + byteToInt(rawBuffer[4]) * 256 + byteToInt(rawBuffer[5]);

                log.info("Decoded signature packet - Stroke Length: {} bytes, JPEG Image Length: {} bytes", len1, len2);

                if (len2 <= 0 || (6 + len1 + len2) > rawBuffer.length) {
                    throw new SignaturePadException("Invalid signature image payload received from device (len2=" + len2 + ").",
                            500, HttpStatus.INTERNAL_SERVER_ERROR);
                }

                // Extract JPEG image bytes
                imageBytes = Arrays.copyOfRange(rawBuffer, 6 + len1, 6 + len1 + len2);

            } finally {
                log.debug("Closing USB connection to FP430S device...");
                try {
                    int closeStatus = fp430SLibrary.USB_Close();
                    log.info("FP430S USB_Close returned status: {}", closeStatus);
                } catch (Exception e) {
                    log.warn("Error while closing USB connection: {}", e.getMessage());
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SignaturePadException("Signature capture operation was interrupted.", 500, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (lockAcquired) {
                hardwareLock.unlock();
            }
        }

        // Convert image to Base64
        String rawBase64 = Base64.getEncoder().encodeToString(imageBytes);
        String dataUrl = PNG_DATA_URL_PREFIX + rawBase64;
        long durationMs = System.currentTimeMillis() - startTime;

        // Optionally save to disk
        String savedFilePath = null;
        if (saveToDisk) {
            savedFilePath = saveImageToDisk(imageBytes, customFilename);
        }

        return SignatureDataDto.builder()
                .signatureDataUrl(dataUrl)
                .imageBase64(rawBase64)
                .imageSizeBytes(imageBytes.length)
                .strokeDataLength(len1)
                .imageFormat("png")
                .savedFilePath(savedFilePath)
                .durationMs(durationMs)
                .captureTime(LocalDateTime.now())
                .build();
    }

    @Override
    public byte[] captureSignatureRawImage(SignatureCaptureRequest request) {
        SignatureDataDto dto = captureSignature(request);
        return Base64.getDecoder().decode(dto.getImageBase64());
    }

    @Override
    public boolean pingDevice() {
        if (!hardwareLock.tryLock()) {
            return true; // device is in active use, meaning it is present
        }
        try {
            int openStatus = fp430SLibrary.USB_Open();
            if (openStatus == 0) {
                fp430SLibrary.USB_Close();
                return true;
            }
            return false;
        } catch (Exception e) {
            log.warn("Device ping failed: {}", e.getMessage());
            return false;
        } finally {
            hardwareLock.unlock();
        }
    }

    private int resolveTimeout(SignatureCaptureRequest request) {
        if (request != null) {
            if (request.getTimeoutMs() != null && request.getTimeoutMs() > 0) {
                return request.getTimeoutMs();
            }
            if (request.getTimeoutSeconds() != null && request.getTimeoutSeconds() > 0) {
                return request.getTimeoutSeconds() * 1000;
            }
        }
        return properties.getTimeoutMs();
    }

    private int byteToInt(byte b) {
        return (int) (b & 0xFF);
    }

    private String saveImageToDisk(byte[] imageBytes, String customFilename) {
        try {
            String dirPath = properties.getOutputDir();
            Files.createDirectories(Paths.get(dirPath));

            String filename = (customFilename != null && !customFilename.trim().isEmpty())
                    ? customFilename.trim()
                    : "signature_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS")) + ".png";

            File targetFile = new File(dirPath, filename);
            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                fos.write(imageBytes);
                fos.flush();
            }
            log.info("Saved signature image to disk: {}", targetFile.getAbsolutePath());
            return targetFile.getAbsolutePath();
        } catch (IOException e) {
            log.error("Failed to save signature image to disk: {}", e.getMessage(), e);
            return null;
        }
    }
}
