package com.funkanalytics.populations.data.manager

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.ArrayList

@Configuration
@EnableSwagger2
class SwaggerConfiguration {
    val DEFAULT_CONTACT = Contact(
            "Aaron Funk Taylor",
            "https://github.com/funkanalytics/",
            "aaron@funkanalytics.com"
    )

    val DEFAULT_API_INFO = ApiInfo(
            "Populations Data Manager",
            "In real-time, manages state store of City Populations data across distributed systems",
            "1.0",
            "urn:tos",
            DEFAULT_CONTACT,
            "Apache 2.0",
            "http://www.apache.org/licenses/LICENSE-2.0",
            ArrayList()
    )

    @Bean
    open fun api(): Docket = Docket(DocumentationType.SWAGGER_2)
            .apiInfo(DEFAULT_API_INFO)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
}