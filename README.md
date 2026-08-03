# FP430S Signature Pad REST API Service

Spring Boot REST service for the **FP430S** electronic signature pad hardware.

---

## 🚀 Features

- **Single Core REST API Endpoint**: `POST /capture-signature` (also available via `GET /capture-signature` and `/api/v1/capture-signature`).
- **Direct Image Streaming**: `GET /capture-signature/image` returns raw `image/jpeg` stream.
- **Base64 Data URL & Metadata**: Returns standard JPEG Base64 and HTML Data URL for direct UI rendering.
- **Hardware Thread-Safety**: Concurrency lock prevents conflicting requests from corrupting the device buffer.
- **Auto Resource Extraction**: Native 64-bit / 32-bit `FP430S.dll` libraries are bundled in `resources` and loaded dynamically.
- **CORS Enabled**: Ready for browser frontends, React, Angular, Vue, and Electron kiosk applications.
- **Swagger / OpenAPI Documentation**: Built-in interactive UI at `http://localhost:8089/swagger-ui.html`.

---

## 📡 API Specification

### Endpoint: Capture Signature

| Method | Path | Description |
|---|---|---|
| `POST` | `/capture-signature` | Captures signature from hardware and returns Base64 & metadata |
| `GET`  | `/capture-signature` | GET variant with query parameters |
| `GET`  | `/capture-signature/image` | Returns raw `image/jpeg` binary stream |
| `POST` | `/api/v1/capture-signature` | Alias route |
| `GET`  | `/api/v1/capture-signature` | Alias route |

---

### Request (POST `/capture-signature`)

```json
{
  "timeoutSeconds": 30,
  "timeoutMs": 30000,
  "encryptType": 0,
  "saveToDisk": false,
  "filename": "signature_custom.jpg"
}
```

*All fields are optional.*

---

### Response (JSON)

```json
{
  "success": true,
  "code": 0,
  "message": "Signature captured successfully",
  "data": {
    "signatureDataUrl": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD...",
    "imageBase64": "/9j/4AAQSkZJRgABAQEASABIAAD...",
    "imageSizeBytes": 24580,
    "strokeDataLength": 350,
    "imageFormat": "jpeg",
    "savedFilePath": null,
    "durationMs": 3450,
    "captureTime": "2026-08-02T16:55:00"
  },
  "timestamp": "2026-08-02T16:55:00.123"
}
```

---

### Error Responses

- **408 Request Timeout / Cancelled**:
  ```json
  {
    "success": false,
    "code": 408,
    "message": "Signature capture cancelled or timed out (device error code: -1).",
    "timestamp": "2026-08-02T16:55:30.000"
  }
  ```

- **503 Service Unavailable (Device Disconnected)**:
  ```json
  {
    "success": false,
    "code": 503,
    "message": "Failed to open FP430S USB signature pad. Please check USB connection.",
    "timestamp": "2026-08-02T16:55:00.000"
  }
  ```

- **409 Conflict (Device Busy)**:
  ```json
  {
    "success": false,
    "code": 409,
    "message": "Signature pad is currently in use by another request. Please try again.",
    "timestamp": "2026-08-02T16:55:00.000"
  }
  ```

---

## 🛠️ Configuration (`application.yml`)

```yaml
server:
  port: 8089

signature:
  pad:
    timeout-ms: 60000       # Default capture timeout in ms (60 seconds)
    encrypt-type: 0         # 0: None / standard
    save-to-disk: false     # Whether to save JPEG to disk
    output-dir: "C:/temp/signatures"
    dll-path: ""            # Optional custom path to FP430S.dll
```

---

## 💻 How to Run

### 1. Build:
```bash
mvn clean package
```

### 2. Run:
```bash
java -jar target/signature-pad-api-1.0.0.jar
```

Or using Maven directly:
```bash
mvn spring-boot:run
```

---

## 🧪 Testing with cURL / Postman / Browser

### 1. Swagger UI:
Open: `http://localhost:8089/swagger-ui.html`

### 2. cURL (POST):
```bash
curl -X POST http://localhost:8089/capture-signature \
     -H "Content-Type: application/json" \
     -d '{"timeoutSeconds": 30}'
```

### 3. cURL (GET):
```bash
curl "http://localhost:8089/capture-signature?timeoutSeconds=30"
```

### 4. Fetch Direct Image (HTML <img>):
```html
<img src="http://localhost:8089/capture-signature/image?timeoutSeconds=30" alt="Signature" />
```
