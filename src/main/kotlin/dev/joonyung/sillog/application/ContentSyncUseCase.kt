package dev.joonyung.sillog.application

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.File

/**
 * Git-based content synchronization logic.
 * Pulls latest changes from remote repository.
 */
@Component
class ContentSyncUseCase {

    private val log = LoggerFactory.getLogger(javaClass)
    private val contentDir = File("content")

    /**
     * Executes git pull on content directory.
     * Returns true if sync was successful or skipped, false on error.
     */
    suspend fun sync(): Boolean = withContext(Dispatchers.IO) {
        if (!contentDir.exists() || !File(contentDir, ".git").exists()) {
            log.warn("Content directory is not a git repository. Skipping sync.")
            return@withContext false
        }

        val process = ProcessBuilder("git", "pull", "--ff-only")
            .directory(contentDir)
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().readText().trim()
        val exitCode = process.waitFor()

        when {
            exitCode != 0 -> {
                log.error("Content sync failed (exit=$exitCode):\n$output")
                false
            }
            output.contains("Already up to date") -> {
                log.debug("Content sync: no changes")
                true
            }
            else -> {
                log.info("Content sync completed:\n$output")
                true
            }
        }
    }
}
