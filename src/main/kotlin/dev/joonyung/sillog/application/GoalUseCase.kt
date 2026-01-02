package dev.joonyung.sillog.application

import dev.joonyung.sillog.domain.Goal
import dev.joonyung.sillog.domain.GoalRepository
import org.springframework.stereotype.Service

data class GoalNodeView(
    val id: String,
    val title: String,
    val isLeaf: Boolean,
    val status: String,
    val completedAt: String?,
    val completionPercent: Int,
    val children: List<GoalNodeView>,
    val depth: Int
)

@Service
class GoalUseCase(
    private val goalRepository: GoalRepository
) {
    suspend fun getGoalTree(): List<GoalNodeView> {
        val goals = goalRepository.findAll()
        return goals.map { goal -> toNodeView(goal, depth = 0) }
    }

    private fun toNodeView(goal: Goal, depth: Int): GoalNodeView {
        val children = goal.children.map { child -> toNodeView(child, depth + 1) }
        val derivedStatus = deriveStatus(goal, children)
        val derivedCompletedAt = deriveCompletedAt(goal, children)

        return GoalNodeView(
            id = goal.id.value,
            title = goal.title,
            isLeaf = goal.isLeaf,
            status = derivedStatus,
            completedAt = derivedCompletedAt,
            completionPercent = calculatePercent(goal, children),
            children = children,
            depth = depth
        )
    }

    private fun deriveStatus(goal: Goal, children: List<GoalNodeView>): String {
        if (goal.isLeaf) {
            return goal.status?.name ?: "PENDING"
        }
        val childStatuses = children.map { it.status }
        return when {
            childStatuses.all { it == "COMPLETED" } -> "COMPLETED"
            childStatuses.any { it != "PENDING" } -> "IN_PROGRESS"
            else -> "PENDING"
        }
    }

    private fun deriveCompletedAt(goal: Goal, children: List<GoalNodeView>): String? {
        if (goal.isLeaf) {
            return goal.completedAt?.let { formatDate(it) }
        }
        if (children.all { it.status == "COMPLETED" }) {
            return children.mapNotNull { it.completedAt }.maxOrNull()
        }
        return null
    }

    private fun calculatePercent(goal: Goal, children: List<GoalNodeView>): Int {
        if (goal.isLeaf) {
            return if (goal.status == Goal.Status.COMPLETED) 100 else 0
        }
        val rates = children.map { it.completionPercent }
        return if (rates.isEmpty()) 0 else rates.average().toInt()
    }

    private fun formatDate(date: java.time.LocalDate): String {
        return "${date.year}. ${date.monthValue}. ${date.dayOfMonth}"
    }
}

