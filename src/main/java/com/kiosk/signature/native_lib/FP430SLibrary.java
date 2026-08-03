package com.kiosk.signature.native_lib;

import com.sun.jna.Library;

/**
 * JNA Library interface for Shenzhen Szfp / Smajayu FP430S Signature Pad native DLL.
 */
public interface FP430SLibrary extends Library {

    /**
     * Opens connection to the USB signature pad device.
     * @return 0 on success, negative/non-zero on error.
     */
    int USB_Open();

    /**
     * Closes connection to the USB signature pad device.
     * @return 0 on success, non-zero on error.
     */
    int USB_Close();

    /**
     * Prompts for signature on the pad and captures stroke and image data.
     * 
     * @param encryptType 0: None (standard), 1: DES, 2: AES
     * @param signData Buffer to receive signature data (must be at least 400KB)
     * @param timeout Timeout in milliseconds (e.g. 60000)
     * @return 0 on success, non-zero on error or timeout
     */
    int Lib_GetSignature(int encryptType, byte[] signData, int timeout);

    /**
     * Sets logo image on the pad screen.
     */
    int Lib_SetLogo(byte[] data, int len, int timeout);

    /**
     * Initiates screen alignment / calibration.
     */
    int Lib_Alignment(int timeout);

    /**
     * Clears customer info shown on screen.
     */
    int Lib_ClearCustomerInfo();

    /**
     * Adds an item of customer info.
     */
    int Lib_AddCustomerInfoItem(byte[] attr, byte[] value);

    /**
     * Shows customer info on the pad screen.
     */
    int Lib_ShowCustomerInfo(int timeout);
}
