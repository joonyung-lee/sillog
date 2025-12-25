package dev.joonyung.joonlog.inbound.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class WebConfig : WebFluxConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // content/images/ 디렉토리를 /images/** 경로로 서빙
        registry.addResourceHandler("/images/**")
            .addResourceLocations("file:content/images/")
    }
}

