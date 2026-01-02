package dev.joonyung.sillog.domain

interface BlogPostRepository {
    suspend fun findAll(): List<BlogPostSummary>
    suspend fun findBySlug(slug: String): BlogPost?
}

