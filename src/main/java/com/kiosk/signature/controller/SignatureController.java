package com.kiosk.signature.controller;

import com.kiosk.signature.dto.ApiResponse;
import com.kiosk.signature.dto.SignatureCaptureRequest;
import com.kiosk.signature.dto.SignatureDataDto;
import com.kiosk.signature.service.SignaturePadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@Tag(name = "Signature Capture API", description = "Endpoints for interacting with the FP430S hardware signature pad")
public class SignatureController {

    private static final Logger log = LoggerFactory.getLogger(SignatureController.class);
    private final SignaturePadService signaturePadService;

    public SignatureController(SignaturePadService signaturePadService) {
        this.signaturePadService = signaturePadService;
    }

    /**
     * Primary REST endpoint to capture signature from FP430S pad (POST).
     */
    @PostMapping(
            value =  "/api/capture-signature",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Capture Signature (POST)",
            description = "Triggers the FP430S signature pad screen, waits for the user to draw their signature, and returns the Base64 encoded JPEG image."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Signature captured successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "408", description = "Capture timed out or cancelled"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "503", description = "FP430S device disconnected or unavailable")
    })
    public ResponseEntity<ApiResponse<SignatureDataDto>> captureSignaturePost(
            @RequestBody(required = false) SignatureCaptureRequest request) {

        log.info("Received POST /capture-signature request");
        SignatureDataDto result = signaturePadService.captureSignature(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Signature captured successfully"));
    }

    /**
     * Alternative GET endpoint to capture signature with query parameters.
     */
    @GetMapping(
            value = "/api/capture-signature",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Capture Signature (GET)",
            description = "GET variant for capturing signature with optional query parameters."
    )
    public ResponseEntity<?> captureSignatureGet(
            @Parameter(description = "Timeout in seconds (e.g. 30)", example = "30")
            @RequestParam(name = "timeoutSeconds", required = false) Integer timeoutSeconds,

            @Parameter(description = "Timeout in milliseconds (e.g. 60000)", example = "60000")
            @RequestParam(name = "timeoutMs", required = false) Integer timeoutMs,

            @Parameter(description = "Response format: 'json' (default) or 'image' (returns raw image/png binary stream)", example = "json")
            @RequestParam(name = "format", required = false, defaultValue = "json") String format,

            @Parameter(description = "Whether to save image on server disk", example = "false")
            @RequestParam(name = "saveToDisk", required = false) Boolean saveToDisk,

            @Parameter(description = "Custom filename if saving to disk", example = "sign.png")
            @RequestParam(name = "filename", required = false) String filename) {

        SignatureCaptureRequest request = SignatureCaptureRequest.builder()
                .timeoutSeconds(timeoutSeconds)
                .timeoutMs(timeoutMs)
                .saveToDisk(saveToDisk)
                .filename(filename)
                .build();

        if ("image".equalsIgnoreCase(format) || "binary".equalsIgnoreCase(format) || "png".equalsIgnoreCase(format)) {
            byte[] imageBytes = signaturePadService.captureSignatureRawImage(request);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"signature.png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(imageBytes);
        }

        SignatureDataDto result = signaturePadService.captureSignature(request);
        return ResponseEntity.ok(ApiResponse.success(result, "Signature captured successfully"));
    }

    /**
     * Direct PNG image stream endpoint for direct embedding in HTML <img> tags or browser viewing.
     */
    @GetMapping(
            value = "/api/capture-signature/image",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    @Operation(
            summary = "Capture Signature Image Stream",
            description = "Captures signature and streams raw PNG binary image directly with Content-Type: image/png."
    )
    public ResponseEntity<byte[]> captureSignatureImage(
            @RequestParam(name = "timeoutSeconds", required = false) Integer timeoutSeconds) {

        SignatureCaptureRequest request = SignatureCaptureRequest.builder()
                .timeoutSeconds(timeoutSeconds)
                .build();

        byte[] imageBytes = signaturePadService.captureSignatureRawImage(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"signature.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}
