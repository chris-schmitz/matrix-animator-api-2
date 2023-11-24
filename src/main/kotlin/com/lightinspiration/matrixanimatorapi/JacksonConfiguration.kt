package com.lightinspiration.matrixanimatorapi

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class JacksonConfiguration {

    @Primary
    fun objectMapper(): ObjectMapper{
        return ObjectMapper()
            .findAndRegisterModules()
    }
}