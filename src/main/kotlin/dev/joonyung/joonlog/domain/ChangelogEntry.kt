package dev.joonyung.joonlog.domain

import java.time.LocalDate

data class ChangelogEntry(
    val date: LocalDate,
    val contentHtml: String
)

