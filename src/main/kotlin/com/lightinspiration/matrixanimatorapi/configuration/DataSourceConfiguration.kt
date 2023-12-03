package com.lightinspiration.matrixanimatorapi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class DataSourceConfiguration {

    // TODO: review  https://github.com/FoodEssentials/example-service-multi-module-api/blob/46d36898ba0e891b0753f01032e91989a4f6c5fe/example-service-multi-module-api/src/main/kotlin/com/labelinsight/example/configuration/DatabaseConfiguration.kt#L17-L21
    @Bean
    fun postgresDataSource(): DataSource {
        val source = DriverManagerDataSource()
        source.apply {
            setDriverClassName("org.postgresql.Driver")
            url = "jdbc:postgresql://localhost:5432/schmitz_sandbox"
            //url = "jdbc:postgresql://localhost:5432/matrix_animator"
            username = "postgres"
            password = "password"
        }
        return source
    }

    // TODO review our structure, where does this come in play vs a DataSource
    //@Bean("postgres-named-template")
    //fun postgresNamedTempate(): NamedParameterJdbcTemplate {
    //    //return NamedParameterJdbcTemplate()
    //}
}