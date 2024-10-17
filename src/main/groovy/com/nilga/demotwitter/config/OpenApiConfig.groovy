package com.nilga.demotwitter.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

/**
 * Configuration class for OpenAPI (Swagger) documentation.
 * Sets up the basic information for the Demo Twitter API.
 */
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Demo Twitter API",
        version = "v1",
        description = "API documentation for the Demo Twitter application"
))
class OpenApiConfig {
}
