package dev.joonyung.sillog.outbound

import dev.joonyung.sillog.domain.BlogPost
import dev.joonyung.sillog.domain.BlogPostRepository
import dev.joonyung.sillog.domain.BlogPostSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.stereotype.Repository
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText

@Repository
class MarkdownBlogPostRepository : BlogPostRepository {

    private val blogDir: Path = Paths.get("content/blog")
    private val parser: Parser = Parser.builder().build()
    private val renderer: HtmlRenderer = HtmlRenderer.builder().build()

    override suspend fun findAll(): List<BlogPostSummary> = withContext(Dispatchers.IO) {
        if (!blogDir.exists()) return@withContext emptyList()

        Files.list(blogDir).use { stream ->
            stream.toList()
                .filter { it.extension == "md" }
                .mapNotNull { parseSummary(it) }
                .sortedByDescending { it.date }
        }
    }

    override suspend fun findBySlug(slug: String): BlogPost? = withContext(Dispatchers.IO) {
        val file = blogDir.resolve("$slug.md")
        if (!file.exists()) return@withContext null
        parsePost(file)
    }

    private fun parseSummary(file: Path): BlogPostSummary? {
        return try {
            val content = file.readText()
            val (frontmatter, _) = parseFrontmatter(content)

            BlogPostSummary(
                slug = file.nameWithoutExtension,
                title = frontmatter["title"]?.toString() ?: return null,
                date = frontmatter["date"]?.let { parseDate(it) } ?: return null,
                summary = frontmatter["summary"]?.toString()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parsePost(file: Path): BlogPost? {
        return try {
            val content = file.readText()
            val (frontmatter, body) = parseFrontmatter(content)

            val document = parser.parse(body)
            val html = renderer.render(document)

            BlogPost(
                slug = file.nameWithoutExtension,
                title = frontmatter["title"]?.toString() ?: return null,
                date = frontmatter["date"]?.let { parseDate(it) } ?: return null,
                summary = frontmatter["summary"]?.toString(),
                contentHtml = html
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseFrontmatter(content: String): Pair<Map<String, Any>, String> {
        if (!content.startsWith("---")) {
            return emptyMap<String, Any>() to content
        }

        val endIndex = content.indexOf("---", 3)
        if (endIndex == -1) {
            return emptyMap<String, Any>() to content
        }

        val frontmatterText = content.substring(3, endIndex).trim()
        val body = content.substring(endIndex + 3).trim()

        val frontmatter = mutableMapOf<String, Any>()
        frontmatterText.lines().forEach { line ->
            val colonIndex = line.indexOf(':')
            if (colonIndex > 0) {
                val key = line.substring(0, colonIndex).trim()
                val value = line.substring(colonIndex + 1).trim()
                frontmatter[key] = value
            }
        }

        return frontmatter to body
    }

    private fun parseDate(value: Any): LocalDate {
        return when (value) {
            is Date -> value.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            is String -> LocalDate.parse(value)
            else -> throw IllegalArgumentException("Cannot parse date: $value")
        }
    }
}

