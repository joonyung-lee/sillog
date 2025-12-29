package dev.joonyung.sillog.inbound.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class WebConfig : WebFluxConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Serve static files from content/changelog/
        // Pattern: /changelog/2025-12-31/img.png → content/changelog/2025-12-31/img.png
        // In production, Caddy filters to images only; here we serve all for simplicity
        registry.addResourceHandler("/changelog/**")
            .addResourceLocations("file:content/changelog/")
    }
}

