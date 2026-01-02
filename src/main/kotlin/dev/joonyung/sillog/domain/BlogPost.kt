package dev.joonyung.sillog.domain

import java.time.LocalDate

data class BlogPost(
    val slug: String,
    val title: String,
    val date: LocalDate,
    val summary: String?,
    val contentHtml: String
)

data class BlogPostSummary(
    val slug: String,
    val title: String,
    val date: LocalDate,
    val summary: String?
)

