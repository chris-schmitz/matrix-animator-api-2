package com.lightinspiration.matrixanimatorapi.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
@Profile("!integration-test")
class DataSourceConfiguration {

    // TODO: review  https://github.com/FoodEssentials/example-service-multi-module-api/blob/46d36898ba0e891b0753f01032e91989a4f6c5fe/example-service-multi-module-api/src/main/kotlin/com/labelinsight/example/configuration/DatabaseConfiguration.kt#L17-L21
    // * The datasource defines the information needed to connect to a Database to access the various schemas->tables->records inside
    // * our queries from the template level happen within a given database but need to specify the target data starting at the schema level
    @Bean("Postgres DataSource")
    @ConfigurationProperties("spring.datasource.schmitz-sandbox")
    fun postgresDataSource(): DataSource {
        return DataSourceBuilder.create().build()
    }

}