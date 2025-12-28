package dev.joonyung.sillog.application

import dev.joonyung.sillog.domain.ChangelogEntry
import org.commonmark.node.Link
import org.commonmark.node.Node
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.AttributeProvider
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.name
import kotlin.io.path.readText

@Service
class ChangelogService {

    private val contentDir: Path = Paths.get("content/changelog")
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val parser: Parser = Parser.builder().build()
    private val renderer: HtmlRenderer = HtmlRenderer.builder()
        .attributeProviderFactory { ExternalLinkAttributeProvider() }
        .build()

    companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }

    /**
     * 모든 엔트리를 날짜 역순(최신 먼저)으로 반환
     */
    fun findAll(): List<ChangelogEntry> {
        if (!contentDir.exists()) return emptyList()

        return Files.list(contentDir)
            .filter { it.isDirectory() && isValidDateDirectory(it.name) }
            .map { parseEntry(it) }
            .toList()
            .filterNotNull()
            .sortedByDescending { it.date }
    }

    /**
     * 특정 날짜 기준으로 앞뒤 데이터 반환
     */
    fun findAround(targetDate: LocalDate, limit: Int = DEFAULT_PAGE_SIZE): List<ChangelogEntry> {
        val all = findAll()
        val targetIndex = all.indexOfFirst { it.date <= targetDate }.takeIf { it >= 0 } ?: 0
        val start = (targetIndex - limit / 2).coerceAtLeast(0)
        val end = (start + limit).coerceAtMost(all.size)
        return all.subList(start, end)
    }

    /**
     * 특정 날짜 이전(과거) 엔트리 반환 - 아래로 스크롤용
     * 반환 순서: 날짜 역순 (최신 먼저, 즉 beforeDate에 가까운 것 먼저)
     */
    fun findBefore(beforeDate: LocalDate, limit: Int = DEFAULT_PAGE_SIZE): List<ChangelogEntry> {
        return findAll()
            .filter { it.date < beforeDate }
            .take(limit)
    }

    /**
     * 특정 날짜 이후(미래) 엔트리 반환 - 위로 스크롤용
     * 반환 순서: afterend 삽입 후 최신이 위에 오도록 "오래된 순"으로 반환
     * 예: afterDate=11/15 → [11/17, 11/20, 11/22] (11/17이 먼저 삽입되어 아래로 밀림)
     */
    fun findAfter(afterDate: LocalDate, limit: Int = DEFAULT_PAGE_SIZE): List<ChangelogEntry> {
        return findAll()
            .filter { it.date > afterDate }
            .sortedBy { it.date }  // 오래된 순 (가까운 미래 먼저)
            .take(limit)
    }

    /**
     * 초기 로딩: 최신 N개 반환
     */
    fun findRecent(limit: Int = DEFAULT_PAGE_SIZE): List<ChangelogEntry> {
        return findAll().take(limit)
    }

    /**
     * 특정 월의 기록 있는 날짜 목록
     */
    fun getAvailableDatesForMonth(yearMonth: YearMonth): Set<LocalDate> {
        return findAll()
            .map { it.date }
            .filter { YearMonth.from(it) == yearMonth }
            .toSet()
    }

    /**
     * 가장 최근 기록 날짜
     */
    fun getLatestDate(): LocalDate? = findAll().firstOrNull()?.date

    /**
     * 더 과거 데이터가 있는지
     */
    fun hasMoreBefore(beforeDate: LocalDate): Boolean {
        return findAll().any { it.date < beforeDate }
    }

    /**
     * 더 미래 데이터가 있는지
     */
    fun hasMoreAfter(afterDate: LocalDate): Boolean {
        return findAll().any { it.date > afterDate }
    }

    private fun isValidDateDirectory(name: String): Boolean {
        return try {
            LocalDate.parse(name, dateFormatter)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    private fun parseEntry(directoryPath: Path): ChangelogEntry? {
        return try {
            val date = LocalDate.parse(directoryPath.name, dateFormatter)
            val filePath = directoryPath.resolve("content.md")
            if (!filePath.exists()) return null
            val markdown = filePath.readText()
            val document = parser.parse(markdown)
            val html = renderer.render(document)
            ChangelogEntry(date = date, contentHtml = html)
        } catch (e: Exception) {
            null
        }
    }

    private class ExternalLinkAttributeProvider : AttributeProvider {
        override fun setAttributes(node: Node, tagName: String, attributes: MutableMap<String, String>) {
            if (node is Link && (node.destination.startsWith("http://") || node.destination.startsWith("https://"))) {
                attributes["target"] = "_blank"
                attributes["rel"] = "noopener noreferrer"
            }
        }
    }
}

