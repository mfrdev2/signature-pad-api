package com.kiosk.signature.service;

import com.kiosk.signature.dto.SignatureCaptureRequest;
import com.kiosk.signature.dto.SignatureDataDto;

public interface SignaturePadService {

    /**
     * Captures a signature from the FP430S hardware device.
     *
     * @param request optional parameters (timeout, encryptType, saveToDisk, filename)
     * @return Captured signature metadata and Base64 JPEG data
     */
    SignatureDataDto captureSignature(SignatureCaptureRequest request);

    /**
     * Captures signature and returns the raw JPEG image byte array.
     *
     * @param request optional parameters
     * @return raw JPEG bytes
     */
    byte[] captureSignatureRawImage(SignatureCaptureRequest request);

    /**
     * Checks if the device can be opened.
     *
     * @return true if device connection succeeded
     */
    boolean pingDevice();
}
