package dev.joonyung.sillog.inbound.scheduler

import dev.joonyung.sillog.application.ContentSyncUseCase
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

/**
 * Coroutine-based scheduler for content synchronization.
 * Uses structured concurrency with SupervisorJob.
 */
@Component
class ContentSyncScheduler(
    private val contentSyncUseCase: ContentSyncUseCase
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var job: Job? = null

    companion object {
        private val SYNC_INTERVAL: Duration = 24.hours
        private val INITIAL_DELAY: Duration = 3.seconds
    }

    init {
        job = scope.launch {
            log.info("Content sync scheduler started (interval={})", SYNC_INTERVAL)
            delay(INITIAL_DELAY)

            while (true) {
                runCatching {
                    log.info("Content sync started by scheduler")
                    contentSyncUseCase.sync()
                }.onFailure { log.error("Content sync error", it) }
                delay(SYNC_INTERVAL)
            }
        }
    }

    @PreDestroy
    fun stop() {
        job?.cancel()
        log.info("Content sync scheduler stopped")
    }
}