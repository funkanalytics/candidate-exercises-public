package com.imply.architect.candidateapplication.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.imply.architect.candidateapplication.model.CityPopulation
import com.imply.architect.candidateapplication.properties.DatasetProperties
import com.imply.architect.candidateapplication.repository.CityPopulationRepository
import com.imply.avro.city.Population
import com.opencsv.CSVReader
import dev.vishna.watchservice.asWatchChannel
import io.swagger.annotations.*
import kotlinx.coroutines.channels.consumeEach

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.io.File
import javax.validation.Valid
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.file.DataFileReader
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

@RestController
@RequestMapping("/api")
class CityPopulationController(@Autowired private val cityPopulationRepository : CityPopulationRepository) {
    private val logger = KotlinLogging.logger {}
    @Autowired
    lateinit var properties: DatasetProperties

    //gets all populations
    @GetMapping("/populations")
    fun getAllPopulations() : List<CityPopulation> = cityPopulationRepository.findAll().sortedBy { it.Name }

    @ApiResponses()
    //create or update a population entry
    @PostMapping("/populations")
    fun createPopulation(@Valid @RequestBody population: CityPopulation) : CityPopulation = cityPopulationRepository.save(population)

    // deletes a Population
    @DeleteMapping("/populations")
    fun deleteCityPopulation(@Valid @RequestBody population: CityPopulation) : Unit = cityPopulationRepository.delete(population)

    @GetMapping("/datasets")
    fun listDatasetsInDirectory() = File( ".\\src\\main\\resources\\DataSets").walk().forEach {
       if (it.isFile) logger.info(it.name) }

    // TODO fix response body for start watcher, likely integrate with RSocket/webflux
    @Suppress("IMPLICIT_CAST_TO_ANY")
    @kotlinx.coroutines.ExperimentalCoroutinesApi
    @PostMapping(value = ["/datasets"], params = ["startWatch"])
    fun startFileWatch (@RequestParam startWatch: Boolean,
                        @Autowired properties: DatasetProperties) {
        runBlocking {
            val currentDirectory  = File(this@CityPopulationController.properties.location)
            val watchChannel = currentDirectory.asWatchChannel()

            if (startWatch) launch {
                watchChannel.consumeEach { event ->

                    if (event.kind.toString().toLowerCase() == "initialized") {
                        event.file.listFiles()?.forEach { file -> fileSelector(file) }
                    }
                    if (event.kind.toString().toLowerCase() ==  "modified") {
                        fileSelector(event.file)
                    }
                }

            }
            if (!startWatch) watchChannel.close()
        }
    }

    fun fileSelector(file: File) {
        when (file.extension.toLowerCase()) {
            "csv" -> csvFileProcess(file)
            "json" -> jsonFileProcess(file)
            "avro" -> avroFileProcess(file)
        }
    }

    fun csvFileProcess(file: File) {
        logger.debug("csv file process")
        var fileReader: BufferedReader? = null
        var csvReader: CSVReader? = null

        try {
            fileReader = BufferedReader(FileReader(file))
            csvReader = CSVReader(fileReader)

            var record: Array<String>?
            csvReader.readNext() // skip Header

            record = csvReader.readNext()
            while (record != null) {
                createPopulation(CityPopulation(record[0], record[1], record[2].toLong()))
                record = csvReader.readNext()
            }

            csvReader.close()
        } catch (e: Exception) {
            logger.error("Reading CSV Error!")
            e.printStackTrace()
        } finally {
            try {
                fileReader!!.close()
                csvReader!!.close()
            } catch (e: IOException) {
                logger.error("Closing fileReader/csvParser Error!")
                e.printStackTrace()
            }
        }
    }

    fun jsonFileProcess(file: File) {
        logger.debug("json file process")
        val mapper = jacksonObjectMapper()
        mapper.registerKotlinModule()

        val jsonString: String = File(file.absolutePath).readText(Charsets.UTF_8)
        val jsonTextList:List<CityPopulation> = mapper.readValue(jsonString)
        for (population in jsonTextList) {
            logger.info(population.toString())
        }
    }

    fun avroFileProcess(file: File) {
        logger.debug("avro file process")
        val populationDatumReader = SpecificDatumReader<Population>(Population::class.java)
        val dataFileReader = DataFileReader<Population>(file, populationDatumReader)
        while (dataFileReader.hasNext()) {
            val population = Population.newBuilder(dataFileReader.next())
            if (population.hasName() && population.hasCountryCode()) {
                createPopulation(CityPopulation(population.name, population.countryCode, population.population))
            }
        }
    }
}
