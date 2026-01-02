package dev.joonyung.sillog.outbound

import dev.joonyung.sillog.domain.Goal
import dev.joonyung.sillog.domain.GoalId
import dev.joonyung.sillog.domain.GoalRepository
import dev.joonyung.sillog.domain.Goals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Repository
import org.yaml.snakeyaml.Yaml
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.attribute.FileTime
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlin.io.path.exists

@Repository
class YamlGoalRepository : GoalRepository {
    private val goalsFile: Path = Paths.get("content/goals.yaml")
    private val yaml = Yaml()

    @Volatile
    private var cached: CachedGoals? = null

    private data class CachedGoals(
        val lastModifiedAt: FileTime,
        val goals: Goals
    )

    override suspend fun findAll(): Goals = withContext(Dispatchers.IO) {
        if (!goalsFile.exists()) return@withContext Goals.EMPTY

        val currentModTime = Files.getLastModifiedTime(goalsFile)
        cached?.let { if (it.lastModifiedAt == currentModTime) return@withContext it.goals }

        val content = Files.readString(goalsFile)
        yaml.load<List<Map<String, Any>>>(content)
            ?.map { parseGoal(it, null) }
            ?.let { Goals(it) }
            ?.also { cached = CachedGoals(currentModTime, it) }
            ?: Goals.EMPTY
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseGoal(map: Map<String, Any>, parentId: GoalId?): Goal {
        val id = GoalId(map["id"]!!.toString())
        val title = map["title"]!!.toString()
        val status = map["status"]?.toString()?.let { Goal.Status.valueOf(it) }
        val completedAt = map["completedAt"]?.let { parseDate(it) }
        val childrenRaw = map["children"] as? List<Map<String, Any>>
            ?: emptyList()
        val children = Goals(childrenRaw.map { parseGoal(it, id) })

        return Goal(
            id = id,
            title = title,
            parentId = parentId,
            status = status,
            completedAt = completedAt,
            children = children
        )
    }

    private fun parseDate(value: Any): LocalDate {
        return when (value) {
            is Date -> value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            is String -> LocalDate.parse(value)
            else -> throw IllegalArgumentException("Cannot parse date: $value")
        }
    }
}