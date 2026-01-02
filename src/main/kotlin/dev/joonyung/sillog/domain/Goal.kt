package dev.joonyung.sillog.domain

import java.time.LocalDate

data class Goal(
    val id: GoalId,
    val title: String,
    val status: Status?,
    val completedAt: LocalDate?,
    val parentId: GoalId?,
    val children: Goals
) {
    enum class Status {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }

    val isLeaf: Boolean
        get() = children.isEmpty

    val isRoot: Boolean
        get() = parentId == null

    fun completionRate(completedIds: Set<String>): Double {
        return if (isLeaf) {
            if (id.value in completedIds) 1.0 else 0.0
        } else {
            val rates = children.map { it.completionRate(completedIds) }
            if (rates.isEmpty()) 0.0 else rates.average()
        }
    }

    fun isCompleted(completedIds: Set<String>): Boolean {
        return if (isLeaf) {
            id.value in completedIds
        } else {
            children.all { it.isCompleted(completedIds) }
        }
    }
}

@JvmInline
value class GoalId(val value: String) {

}

@JvmInline
value class Goals(val list: List<Goal>) {
    val isEmpty: Boolean
        get() = list.isEmpty()

    val lastIndex: Int
        get() = list.lastIndex

    fun <R> map(transform: (Goal) -> R): List<R> = list.map(transform)

    fun <R> mapIndexed(transform: (Int, Goal) -> R): List<R> = list.mapIndexed(transform)

    fun all(predicate: (Goal) -> Boolean): Boolean = list.all(predicate)

    fun any(predicate: (Goal) -> Boolean): Boolean = list.any(predicate)

    companion object {
        val EMPTY = Goals(listOf())
    }
}
