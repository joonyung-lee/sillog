package dev.joonyung.joonlog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JoonlogApplication

fun main(args: Array<String>) {
	runApplication<JoonlogApplication>(*args)
}
