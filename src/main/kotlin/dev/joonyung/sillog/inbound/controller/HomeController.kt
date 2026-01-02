package dev.joonyung.sillog.inbound.controller

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.reactive.result.view.RedirectView

@Controller
class HomeController {

    @GetMapping("/")
    fun home(): RedirectView {
        return RedirectView("/changelog", HttpStatus.FOUND)
    }

    @GetMapping("/about", produces = [MediaType.TEXT_HTML_VALUE])
    fun about(model: Model): String {
        model.addAttribute("title", "About - sillog")
        model.addAttribute("currentPage", "about")
        return "pages/about"
    }

}
