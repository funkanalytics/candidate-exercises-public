package com.imply.architect.candidateapplication.controller

import com.imply.architect.candidateapplication.model.CityPopulation
import com.imply.architect.candidateapplication.repository.CityPopulationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api")
class CityPopulationController(@Autowired private val cityPopulationRepository : CityPopulationRepository) {
    //gets all populations
    @GetMapping("/populations")
    fun getAllPopulations() : List<CityPopulation> = cityPopulationRepository.findAll().sortedBy { it.name }

    //creates a population entry
    @PostMapping("/populations")
    fun createPopulation(@Valid @RequestBody population: CityPopulation) : CityPopulation = cityPopulationRepository.save(population)

    //gets a single population
    @GetMapping("/populations/{populationId}")
    fun getPopulationById(@PathVariable populationId : Long) : ResponseEntity<CityPopulation> =
            cityPopulationRepository.findById(populationId).map {
                ResponseEntity.ok(it)
            }.orElse(ResponseEntity.notFound().build())

    //updates a population
    @PutMapping("/populations/{populationId}")
    fun updateCityPopulation(@PathVariable populationId : Long, @Valid @RequestBody updatedCityPopulation: CityPopulation)
            : ResponseEntity<CityPopulation> =
            cityPopulationRepository.findById(populationId).map{
                val newPopulation = it.copy(
                        name = updatedCityPopulation.name,
                        code =  updatedCityPopulation.code,
                        Population = updatedCityPopulation.Population)
                ResponseEntity.ok().body(cityPopulationRepository.save(newPopulation))
            }.orElse(ResponseEntity.notFound().build())

    // deletes a Population
    @DeleteMapping("/populations/{populationId}")
    fun deleteCityPopulation(@PathVariable populationId: Long) : ResponseEntity<Void> =
            cityPopulationRepository.findById(populationId).map{
                cityPopulationRepository.delete(it)
                ResponseEntity<Void>(HttpStatus.OK)
            }.orElse(ResponseEntity.notFound().build())
}