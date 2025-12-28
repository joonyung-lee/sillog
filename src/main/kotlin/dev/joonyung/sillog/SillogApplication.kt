package dev.joonyung.sillog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SillogApplication

fun main(args: Array<String>) {
	runApplication<SillogApplication>(*args)
}

