package com.funkanalytics.populations.data.manager.repository

import com.funkanalytics.populations.data.manager.model.CityPopulation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CityPopulationRepository : JpaRepository<CityPopulation, Long> {

}