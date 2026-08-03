package com.kiosk.signature.native_lib;

import com.kiosk.signature.config.SignaturePadProperties;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Configuration
public class NativeLoader {

    private static final Logger log = LoggerFactory.getLogger(NativeLoader.class);
    private final SignaturePadProperties properties;

    public NativeLoader(SignaturePadProperties properties) {
        this.properties = properties;
    }

    @Bean
    public FP430SLibrary fp430SLibrary() {
        log.info("Initializing FP430S Native Library loader...");

        // 1. Check custom path if configured
        if (properties.getDllPath() != null && !properties.getDllPath().trim().isEmpty()) {
            File customFile = new File(properties.getDllPath().trim());
            if (customFile.exists()) {
                String searchDir = customFile.isDirectory() ? customFile.getAbsolutePath() : customFile.getParent();
                log.info("Using configured custom DLL search path: {}", searchDir);
                NativeLibrary.addSearchPath("FP430S", searchDir);
                appendJnaLibraryPath(searchDir);
            } else {
                log.warn("Configured dllPath '{}' does not exist. Falling back to default search paths.", properties.getDllPath());
            }
        }

        // 2. Determine OS architecture
        String osArch = System.getProperty("os.arch", "").toLowerCase();
        boolean is64Bit = osArch.contains("64");
        String subDir = is64Bit ? "win32-x86-64" : "win32-x86";
        log.info("Detected OS architecture: {} (Target directory: {})", osArch, subDir);

        // 3. Check local directory paths
        String[] localCandidatePaths = new String[]{
                Paths.get("dll", subDir).toAbsolutePath().toString(),
                Paths.get("dll").toAbsolutePath().toString(),
                Paths.get(".").toAbsolutePath().toString()
        };

        boolean foundLocal = false;
        for (String candidate : localCandidatePaths) {
            File dllFile = new File(candidate, "FP430S.dll");
            if (dllFile.exists()) {
                log.info("Found native FP430S.dll at: {}", dllFile.getAbsolutePath());
                NativeLibrary.addSearchPath("FP430S", candidate);
                appendJnaLibraryPath(candidate);
                foundLocal = true;
                break;
            }
        }

        // 4. If not found locally, extract from classpath resources
        if (!foundLocal) {
            String resourcePath = "/" + subDir + "/FP430S.dll";
            try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
                if (in != null) {
                    Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "fp430s_native", subDir);
                    Files.createDirectories(tempDir);
                    Path targetDll = tempDir.resolve("FP430S.dll");
                    Files.copy(in, targetDll, StandardCopyOption.REPLACE_EXISTING);
                    log.info("Extracted FP430S.dll from resource '{}' to: {}", resourcePath, targetDll.toAbsolutePath());
                    NativeLibrary.addSearchPath("FP430S", tempDir.toAbsolutePath().toString());
                    appendJnaLibraryPath(tempDir.toAbsolutePath().toString());
                } else {
                    log.warn("Classpath resource '{}' not found. Relying on default system PATH.", resourcePath);
                }
            } catch (Exception e) {
                log.error("Failed to extract FP430S.dll from resources: {}", e.getMessage(), e);
            }
        }

        log.info("Loading FP430S JNA instance...");
        try {
            FP430SLibrary instance = Native.load("FP430S", FP430SLibrary.class);
            log.info("FP430S Native Library loaded successfully.");
            return instance;
        } catch (UnsatisfiedLinkError e) {
            log.error("Failed to load FP430S native library! Ensure FP430S.dll (and MSVC runtime dependencies) are present: {}", e.getMessage());
            throw e;
        }
    }

    private void appendJnaLibraryPath(String path) {
        String currentPath = System.getProperty("jna.library.path", "");
        if (!currentPath.contains(path)) {
            System.setProperty("jna.library.path", currentPath.isEmpty() ? path : currentPath + File.pathSeparator + path);
        }
    }
}
