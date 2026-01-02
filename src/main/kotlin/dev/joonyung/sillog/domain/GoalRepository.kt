package dev.joonyung.sillog.domain

interface GoalRepository {
    suspend fun findAll(): Goals
}

