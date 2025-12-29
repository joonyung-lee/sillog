package dev.joonyung.sillog.inbound.controller

import dev.joonyung.sillog.application.ContentSyncUseCase
import dev.joonyung.sillog.inbound.config.WebhookProperties
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/webhook")
class ContentSyncController(
    private val contentSyncUseCase: ContentSyncUseCase,
    private val webhookProperties: WebhookProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping("/content-sync")
    suspend fun triggerSync(
        @RequestHeader("Sillog-Webhook-Api-Key") providedKey: String
    ): ResponseEntity<SyncResponse> {
        if (providedKey != webhookProperties.apiKey) {
            log.warn("Invalid API key attempt from webhook")
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(SyncResponse(false, "Unauthorized"))
        }

        log.info("Webhook triggered: starting content sync")
        val success = contentSyncUseCase.sync()

        return if (success) {
            ResponseEntity.ok(SyncResponse(true, "Sync completed"))
        } else {
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(SyncResponse(false, "Sync failed"))
        }
    }

    data class SyncResponse(
        val success: Boolean,
        val message: String
    )
}
