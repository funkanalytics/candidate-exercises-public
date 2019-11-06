package com.imply.architect.candidateapplication.repository

import com.imply.architect.candidateapplication.model.CityPopulation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CityPopulationRepository : JpaRepository<CityPopulation, Long> {

}