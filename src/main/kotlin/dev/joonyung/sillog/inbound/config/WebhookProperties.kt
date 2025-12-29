package dev.joonyung.sillog.inbound.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "sillog.webhook")
data class WebhookProperties(
    val apiKey: String
) {
    init {
        require(apiKey.isNotBlank()) { "Webhook API key must not be blank." }
    }
}

