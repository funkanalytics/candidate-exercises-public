package com.imply.architect.candidateapplication.model

import java.io.Serializable
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class PopulationId(
        val name: String = "",
        val code: String = "") : Serializable

@Entity(name = "populations")
@IdClass(PopulationId::class)
data class CityPopulation(
        @Id val name : String = "",
        @Id val code : String = "",
        @get: NotNull val Population: Long = 0){}
