package dev.joonyung.joonlog.inbound.controller

import dev.joonyung.joonlog.application.ChangelogService
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ChangelogController(
    private val changelogService: ChangelogService
) {

    @GetMapping("/changelog", produces = [MediaType.TEXT_HTML_VALUE])
    fun changelog(model: Model): String {
        val entriesByYear = changelogService.getEntriesGroupedByYear()
        model.addAttribute("title", "Changelog - joonlog")
        model.addAttribute("currentPage", "changelog")
        model.addAttribute("entriesByYear", entriesByYear)
        return "pages/changelog"
    }

    @GetMapping("/htmx/changelog/entries", produces = [MediaType.TEXT_HTML_VALUE])
    fun entriesFragment(
        @RequestParam(required = false) year: Int?,
        model: Model
    ): String {
        val entriesByYear = if (year != null) {
            mapOf(year to changelogService.findByYear(year))
        } else {
            changelogService.getEntriesGroupedByYear()
        }
        model.addAttribute("entriesByYear", entriesByYear)
        return "fragments/changelog/entries :: entriesList"
    }
}

