package com.lightinspiration.matrixanimatorapi.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import javax.sql.DataSource

@Configuration("src-datasource-configuration")
@Profile("!integration-test")
class DataSourceConfiguration {

    @Primary
    @Bean("Postgres DataSource")
    @ConfigurationProperties("spring.datasource.schmitz-sandbox")
    fun postgresDataSource(): DataSource {
        val dataSource =
            DataSourceBuilder
                .create()
                .build()

        return dataSource
    }

    @Bean("src-named-parameter-jdbc-template")
    fun namedParameterJdbcTemplate(dataSource: DataSource): NamedParameterJdbcTemplate {
        return NamedParameterJdbcTemplate(dataSource)
    }
}