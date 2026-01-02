package dev.joonyung.sillog.inbound.controller

import dev.joonyung.sillog.application.ChangelogService
import dev.joonyung.sillog.application.GoalUseCase
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Controller
class ChangelogController(
    private val changelogService: ChangelogService,
    private val goalUseCase: GoalUseCase
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @GetMapping("/changelog", produces = [MediaType.TEXT_HTML_VALUE])
    fun changelog(
        @RequestParam(required = false) date: String?,
        model: Model
    ): String {
        val targetDate = date?.let { LocalDate.parse(it, dateFormatter) }
        val currentMonth = targetDate?.let { YearMonth.from(it) }
            ?: changelogService.getLatestDate()?.let { YearMonth.from(it) }
            ?: YearMonth.now()

        model.addAttribute("title", "Changelog - sillog")
        model.addAttribute("currentPage", "changelog")
        model.addAttribute("targetDate", targetDate?.format(dateFormatter))
        model.addAttribute("currentMonth", currentMonth.toString())
        return "pages/changelog"
    }

    @GetMapping("/htmx/changelog/entries", produces = [MediaType.TEXT_HTML_VALUE])
    fun entries(
        @RequestParam(required = false) around: String?,
        @RequestParam(defaultValue = "10") limit: Int,
        model: Model
    ): String {
        val entries = if (around != null) {
            changelogService.findAround(LocalDate.parse(around, dateFormatter), limit)
        } else {
            changelogService.findRecent(limit)
        }

        val newest = entries.firstOrNull()?.date
        val oldest = entries.lastOrNull()?.date

        model.addAttribute("entries", entries)
        model.addAttribute("newestDate", newest?.format(dateFormatter))
        model.addAttribute("oldestDate", oldest?.format(dateFormatter))
        model.addAttribute("hasMoreAfter", newest?.let { changelogService.hasMoreAfter(it) } ?: false)
        model.addAttribute("hasMoreBefore", oldest?.let { changelogService.hasMoreBefore(it) } ?: false)
        model.addAttribute("targetDate", around)
        return "fragments/changelog/entries :: entriesList"
    }

    @GetMapping("/htmx/changelog/more-before", produces = [MediaType.TEXT_HTML_VALUE])
    fun moreBefore(
        @RequestParam before: String,
        @RequestParam(defaultValue = "10") limit: Int,
        model: Model
    ): String {
        val beforeDate = LocalDate.parse(before, dateFormatter)
        val entries = changelogService.findBefore(beforeDate, limit)
        val oldest = entries.lastOrNull()?.date

        model.addAttribute("entries", entries)
        model.addAttribute("oldestDate", oldest?.format(dateFormatter))
        model.addAttribute("hasMoreBefore", oldest?.let { changelogService.hasMoreBefore(it) } ?: false)
        return "fragments/changelog/entries :: moreBelow"
    }

    @GetMapping("/htmx/changelog/more-after", produces = [MediaType.TEXT_HTML_VALUE])
    fun moreAfter(
        @RequestParam after: String,
        @RequestParam(defaultValue = "10") limit: Int,
        model: Model
    ): String {
        val afterDate = LocalDate.parse(after, dateFormatter)
        val entries = changelogService.findAfter(afterDate, limit)
        val newest = entries.lastOrNull()?.date  // 오래된 순이므로 마지막이 최신

        model.addAttribute("entries", entries)
        model.addAttribute("newestDate", newest?.format(dateFormatter))
        model.addAttribute("hasMoreAfter", newest?.let { changelogService.hasMoreAfter(it) } ?: false)
        return "fragments/changelog/entries :: moreAbove"
    }

    @GetMapping("/htmx/changelog/calendar", produces = [MediaType.TEXT_HTML_VALUE])
    fun calendar(
        @RequestParam(required = false) month: String?,
        model: Model
    ): String {
        val currentMonth = month?.let { YearMonth.parse(it) }
            ?: changelogService.getLatestDate()?.let { YearMonth.from(it) }
            ?: YearMonth.now()

        val availableDates = changelogService.getAvailableDatesForMonth(currentMonth)
        val firstDay = currentMonth.atDay(1)
        val startOffset = firstDay.dayOfWeek.value % 7

        val calendarDays = buildList {
            repeat(startOffset) { add(null) }
            for (day in 1..currentMonth.lengthOfMonth()) {
                val date = currentMonth.atDay(day)
                add(CalendarDay(
                    day = day,
                    date = date.format(dateFormatter),
                    hasEntry = date in availableDates,
                    isToday = date == LocalDate.now()
                ))
            }
        }

        model.addAttribute("currentMonth", currentMonth)
        model.addAttribute("currentMonthDisplay", currentMonth.format(DateTimeFormatter.ofPattern("yyyy년 M월")))
        model.addAttribute("prevMonth", currentMonth.minusMonths(1).toString())
        model.addAttribute("nextMonth", currentMonth.plusMonths(1).toString())
        model.addAttribute("calendarDays", calendarDays)
        return "fragments/changelog/calendar :: calendar"
    }

    @GetMapping("/htmx/goals/tree", produces = [MediaType.TEXT_HTML_VALUE])
    suspend fun goalTree(model: Model): String {
        val goalTree = goalUseCase.getGoalTree()
        model.addAttribute("goals", goalTree)
        return "fragments/changelog/goal-tree :: goalTree"
    }

    data class CalendarDay(
        val day: Int,
        val date: String,
        val hasEntry: Boolean,
        val isToday: Boolean
    )
}

