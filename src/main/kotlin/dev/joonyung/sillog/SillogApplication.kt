package dev.joonyung.sillog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class SillogApplication

fun main(args: Array<String>) {
	runApplication<SillogApplication>(*args)
}

