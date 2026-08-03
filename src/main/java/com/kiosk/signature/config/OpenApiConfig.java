package com.kiosk.signature.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class OpenApiConfig {

    private static final Logger log = LoggerFactory.getLogger(OpenApiConfig.class);

    @Value("${server.port:8078}")
    private String serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerUiPath;

    @Value("${springdoc.api-docs.path:/v3/api-docs}")
    private String apiDocsPath;

    @Bean
    public OpenAPI signaturePadOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FP430S Signature Pad REST API")
                        .description("REST API service for capturing signatures from the FP430S hardware signature pad device.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("KIOSK Engineering Team")));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void printSwaggerUrl() {
        String baseContext = (contextPath == null || contextPath.trim().isEmpty() || "/".equals(contextPath.trim()))
                ? ""
                : contextPath.trim();
        String swaggerUrl = "http://localhost:" + serverPort + baseContext + swaggerUiPath;
        String apiDocsUrl = "http://localhost:" + serverPort + baseContext + apiDocsPath;

        log.info("--------------------------------------------------------------------------------");
        log.info("Swagger UI documentation available at: {}", swaggerUrl);
        log.info("OpenAPI JSON documentation available at: {}", apiDocsUrl);
        log.info("--------------------------------------------------------------------------------");
    }
}

