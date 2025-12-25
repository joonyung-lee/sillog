package dev.joonyung.joonlog.inbound.controller

import org.springframework.stereotype.Controller
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.ui.Model

@Controller
class HomeController {
	@GetMapping("/", produces = [MediaType.TEXT_HTML_VALUE])
	fun home(model: Model): String {
		model.addAttribute("title", "joonlog")
		model.addAttribute("currentPage", "home")
		return "pages/home"
	}

	@GetMapping("/about", produces = [MediaType.TEXT_HTML_VALUE])
	fun about(model: Model): String {
		model.addAttribute("title", "About - joonlog")
		model.addAttribute("currentPage", "about")
		return "pages/about"
	}

	@GetMapping("/htmx/hello", produces = [MediaType.TEXT_HTML_VALUE])
	fun helloFragment(model: Model): String {
		model.addAttribute("now", java.time.Instant.now().toString())
		return "fragments/hello :: helloResult"
	}
}
