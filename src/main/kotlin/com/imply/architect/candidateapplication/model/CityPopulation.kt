package com.imply.architect.candidateapplication.model

import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class PopulationId(
        val name: String = "",
        val countryCode: String = "") : Serializable

@Entity(name = "population")
@IdClass(PopulationId::class)
data class CityPopulation(
        @Id @get: NotNull val name : String = "",
        @Id @get: NotNull val countryCode : String = "",
        @get: NotNull val population: Long = 0)
