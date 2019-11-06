package com.imply.architect.candidateapplication.model

import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.NotNull

class PopulationId(
        val Name: String = "",
        val CountryCode: String = "") : Serializable

@Entity(name = "population")
@IdClass(PopulationId::class)
data class CityPopulation(
        @Id @get: NotNull val Name : String,
        @Id @get: NotNull val CountryCode : String,
        @get: NotNull val Population: Long = 0)
