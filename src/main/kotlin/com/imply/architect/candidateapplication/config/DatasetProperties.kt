package com.imply.architect.candidateapplication.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix ="datasets")
class DatasetsConfiguration{
        lateinit var populationsDir: String
}