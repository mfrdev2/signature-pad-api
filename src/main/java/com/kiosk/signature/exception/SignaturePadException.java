package com.kiosk.signature.exception;

import org.springframework.http.HttpStatus;

public class SignaturePadException extends RuntimeException {

    private final int errorCode;
    private final HttpStatus httpStatus;

    public SignaturePadException(String message) {
        super(message);
        this.errorCode = -1;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public SignaturePadException(String message, int errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public SignaturePadException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = -1;
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
