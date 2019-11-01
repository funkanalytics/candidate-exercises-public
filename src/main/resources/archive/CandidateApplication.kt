package archive

import org.apache.commons.logging.LogFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.MediaType
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration.ofSeconds
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom

@SpringBootApplication
class CandidateApplication

fun main(args: Array<String>) {
	runApplication<CandidateApplication>(*args)
}

@RestController
class CityPopulationsRestController(private val cityService: CityService) {
	@GetMapping(value = ["/city/{name}"],
			params = ["countryCode"],
			produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
	fun populations(@PathVariable name: String,
					@RequestParam countryCode: String) = cityService.streamOfPopulations(name, countryCode)
}

@Controller
class CityPopulationsRSocketController(private val cityService: CityService) {
	@MessageMapping("cityPopulations")
	fun populations(name: String, countryCode: String) = cityService.streamOfPopulations(name, countryCode)
}

@Service
class CityService {
	private val populationsForCity = ConcurrentHashMap<String, Flux<CityPopulation>>()
	private val log = LogFactory.getLog(javaClass)

	fun streamOfPopulations(name: String,
							countryCode: String): Flux<CityPopulation> {
		return populationsForCity.computeIfAbsent(name+countryCode) {
			Flux
					.interval(ofSeconds(1))
					.map { CityPopulation(name, countryCode, randomCityPopulation()) }
					.doOnSubscribe { log.info("New subscription for city $name, $countryCode.") }
					.share()
		}
	}

	private fun randomCityPopulation() = ThreadLocalRandom.current().nextLong(100L)
}

data class CityPopulation(val name: String,
					      val countryCode: String,
					      val population: Long)

