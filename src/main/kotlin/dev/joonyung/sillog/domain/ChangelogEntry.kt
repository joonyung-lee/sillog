package dev.joonyung.sillog.domain

import java.time.LocalDate

data class ChangelogEntry(
    val date: LocalDate,
    val contentHtml: String
)

