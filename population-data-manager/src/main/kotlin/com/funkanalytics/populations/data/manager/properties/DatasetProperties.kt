package com.funkanalytics.populations.data.manager.properties

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class DatasetProperties {
    @Value("\${dataset.location}")
    lateinit var location: String
}
