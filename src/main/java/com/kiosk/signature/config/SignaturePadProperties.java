package com.kiosk.signature.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "signature.pad")
public class SignaturePadProperties {

    /**
     * Default timeout for signature capture in milliseconds (default: 60,000ms = 60s)
     */
    private int timeoutMs = 60000;

    /**
     * Encrypt type (0: None / Default, 1: DES, 2: AES, etc.)
     */
    private int encryptType = 0;

    /**
     * Whether to save captured signature images to disk
     */
    private boolean saveToDisk = false;

    /**
     * Directory path where signature images will be saved if saveToDisk is true
     */
    private String outputDir = "C:/temp/signatures";

    /**
     * Explicit path to FP430S.dll file or its containing folder (optional)
     */
    private String dllPath = "";

    public SignaturePadProperties() {
    }

    public int getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public int getEncryptType() {
        return encryptType;
    }

    public void setEncryptType(int encryptType) {
        this.encryptType = encryptType;
    }

    public boolean isSaveToDisk() {
        return saveToDisk;
    }

    public void setSaveToDisk(boolean saveToDisk) {
        this.saveToDisk = saveToDisk;
    }

    public String getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    public String getDllPath() {
        return dllPath;
    }

    public void setDllPath(String dllPath) {
        this.dllPath = dllPath;
    }
}
