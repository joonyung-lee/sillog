package dev.joonyung.joonlog.application

import dev.joonyung.joonlog.domain.ChangelogEntry
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
import java.time.format.DateTimeFormatter
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
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

    fun findAll(): List<ChangelogEntry> {
        if (!contentDir.exists()) {
            return emptyList()
        }

        return Files.list(contentDir)
            .filter { it.isRegularFile() && it.name.endsWith(".md") }
            .map { parseEntry(it) }
            .toList()
            .filterNotNull()
            .sortedByDescending { it.date }
    }

    fun findByYear(year: Int): List<ChangelogEntry> {
        return findAll().filter { it.date.year == year }
    }

    fun getYears(): List<Int> {
        return findAll()
            .map { it.date.year }
            .distinct()
            .sortedDescending()
    }

    fun getEntriesGroupedByYear(): Map<Int, List<ChangelogEntry>> {
        return findAll().groupBy { it.date.year }
            .toSortedMap(compareByDescending { it })
    }

    private fun parseEntry(path: Path): ChangelogEntry? {
        return try {
            val filename = path.name.removeSuffix(".md")
            val date = LocalDate.parse(filename, dateFormatter)
            val markdown = path.readText()
            val document = parser.parse(markdown)
            val html = renderer.render(document)
            ChangelogEntry(date = date, contentHtml = html)
        } catch (e: Exception) {
            null
        }
    }

    private class ExternalLinkAttributeProvider : AttributeProvider {
        override fun setAttributes(node: Node, tagName: String, attributes: MutableMap<String, String>) {
            if (node is Link) {
                val url = node.destination
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    attributes["target"] = "_blank"
                    attributes["rel"] = "noopener noreferrer"
                }
            }
        }
    }
}
