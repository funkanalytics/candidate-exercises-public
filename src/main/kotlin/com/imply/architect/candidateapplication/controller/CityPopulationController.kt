package com.imply.architect.candidateapplication.controller

import com.imply.architect.candidateapplication.model.CityPopulation
import com.imply.architect.candidateapplication.repository.CityPopulationRepository
import com.imply.architect.candidateapplication.watcher.KWatchChannel
import com.imply.architect.candidateapplication.watcher.KWatchEvent
import com.imply.architect.candidateapplication.watcher.asWatchChannel
import javafx.application.Application.launch

import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.io.File
import java.time.Duration
import java.time.Duration.ofSeconds
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ThreadLocalRandom
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class CityPopulationController(@Autowired private val cityPopulationRepository : CityPopulationRepository) {
    private val logger = KotlinLogging.logger {}

    //gets all populations
    @GetMapping("/populations")
    fun getAllPopulations() : List<CityPopulation> = cityPopulationRepository.findAll().sortedBy { it.Name }

    //create or update a population entry
    @PostMapping("/populations")
    fun createPopulation(@Valid @RequestBody population: CityPopulation) : CityPopulation = cityPopulationRepository.save(population)

    // deletes a Population
    @DeleteMapping("/populations")
    fun deleteCityPopulation(@Valid @RequestBody population: CityPopulation) : Unit = cityPopulationRepository.delete(population)

    @GetMapping("/populations/datasets")
    fun listDatasetsInDirectory() = File( ".\\src\\main\\resources\\DataSets").walk().forEach {
       if (it.isFile) logger.info(it.name) }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    @PostMapping(value = ["/populations/datasets"], params = ["startWatch"])
    fun startFileWatch (@RequestParam startWatch: Boolean) {
        runBlocking {
            val currentDirectory  = File(".\\src\\main\\resources\\DataSets\\test")
            val watchChannel = currentDirectory.asWatchChannel()

            if (startWatch)  launch {
                watchChannel.consumeEach { event ->
                    logger.info( event.kind.toString())
                    logger.info(event.file.absolutePath.toString())
                }
            } else watchChannel.close()
        }
    }
}
