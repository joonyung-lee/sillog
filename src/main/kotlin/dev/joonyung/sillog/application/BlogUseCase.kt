package dev.joonyung.sillog.application

import dev.joonyung.sillog.domain.BlogPost
import dev.joonyung.sillog.domain.BlogPostRepository
import dev.joonyung.sillog.domain.BlogPostSummary
import org.springframework.stereotype.Component
import java.time.format.DateTimeFormatter

data class BlogPostListView(
    val slug: String,
    val title: String,
    val formattedDate: String,
    val summary: String?
)

data class BlogPostDetailView(
    val slug: String,
    val title: String,
    val formattedDate: String,
    val contentHtml: String
)

@Component
class BlogUseCase(
    private val blogPostRepository: BlogPostRepository
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy. M. d.")

    suspend fun getPostList(): List<BlogPostListView> {
        return blogPostRepository.findAll().map { it.toListView() }
    }

    suspend fun getPost(slug: String): BlogPostDetailView? {
        return blogPostRepository.findBySlug(slug)?.toDetailView()
    }

    private fun BlogPostSummary.toListView() = BlogPostListView(
        slug = slug,
        title = title,
        formattedDate = date.format(dateFormatter),
        summary = summary
    )

    private fun BlogPost.toDetailView() = BlogPostDetailView(
        slug = slug,
        title = title,
        formattedDate = date.format(dateFormatter),
        contentHtml = contentHtml
    )
}

