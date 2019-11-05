package com.imply.architect.candidateapplication.controller

import com.imply.architect.candidateapplication.model.CityPopulation
import org.apache.commons.logging.LogFactory
import org.springframework.http.MediaType
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom

@RestController
class PopulationsRestController(private val cityService: CityService) {
    @GetMapping(value = ["/city/{name}"],
            params = ["countryCode"],
            produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun populations(@PathVariable name: String,
                    @RequestParam countryCode: String) = cityService.streamOfPopulations(name, countryCode)
}

@Controller
class PopulationsRSocketController(private val cityService: CityService) {
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
                    .interval(Duration.ofSeconds(1))
                    .map { CityPopulation(name, countryCode, randomCityPopulation()) }
                    .doOnSubscribe { log.info("New subscription for city $name, $countryCode.") }
                    .share()
        }
    }

    private fun randomCityPopulation() = ThreadLocalRandom.current().nextLong(100L)
}
