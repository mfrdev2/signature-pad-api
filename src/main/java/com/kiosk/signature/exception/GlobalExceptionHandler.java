package com.kiosk.signature.exception;

import com.kiosk.signature.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SignaturePadException.class)
    public ResponseEntity<ApiResponse<Void>> handleSignaturePadException(SignaturePadException ex) {
        log.error("SignaturePadException: [code={}] {}", ex.getErrorCode(), ex.getMessage());
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(UnsatisfiedLinkError.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsatisfiedLinkError(UnsatisfiedLinkError ex) {
        log.error("UnsatisfiedLinkError: Native FP430S.dll could not be loaded: {}", ex.getMessage(), ex);
        String msg = "Native FP430S library error: " + ex.getMessage() + ". Please ensure FP430S.dll is available.";
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unhandled Exception: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "Unexpected internal error: " + ex.getMessage()));
    }
}
