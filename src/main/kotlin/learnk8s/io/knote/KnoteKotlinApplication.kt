package learnk8s.io.knote

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KnoteKotlinApplication

fun main(args: Array<String>) {
	runApplication<KnoteKotlinApplication>(*args)
}
