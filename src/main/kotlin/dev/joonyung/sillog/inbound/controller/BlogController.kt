package dev.joonyung.sillog.inbound.controller

import dev.joonyung.sillog.application.BlogUseCase
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@Controller
class BlogController(
    private val blogUseCase: BlogUseCase
) {
    @GetMapping("/blog", produces = [MediaType.TEXT_HTML_VALUE])
    suspend fun blogList(model: Model): String {
        val posts = blogUseCase.getPostList()
        model.addAttribute("title", "Blog - sillog")
        model.addAttribute("posts", posts)
        model.addAttribute("currentPage", "blog")
        return "pages/blog"
    }

    @GetMapping("/blog/{slug}", produces = [MediaType.TEXT_HTML_VALUE])
    suspend fun blogPost(@PathVariable slug: String, model: Model): String {
        val post = blogUseCase.getPost(slug)
            ?: return "error/404"

        model.addAttribute("title", "${post.title} - sillog")
        model.addAttribute("post", post)
        model.addAttribute("currentPage", "blog")
        return "pages/blog-post"
    }
}

