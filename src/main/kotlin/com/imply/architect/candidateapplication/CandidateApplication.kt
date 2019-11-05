package com.imply.architect.candidateapplication

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CandidateApplication

fun main(args: Array<String>) {
	runApplication<CandidateApplication>(*args)
}