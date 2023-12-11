package com.lightinspiration.matrixanimatorapi.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class JacksonConfiguration {

    @Primary
    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
            .findAndRegisterModules()
    }
}