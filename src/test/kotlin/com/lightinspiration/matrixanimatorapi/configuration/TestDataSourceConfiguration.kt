package com.lightinspiration.matrixanimatorapi.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer
import javax.sql.DataSource

@Configuration("test-datasource-configuration")
@Profile("integration-test")
class TestDataSourceConfiguration {

    // * Note that naming the bean and using its name as a qualifier in the namedParameterJdbcTemplate bean definition _isn't_ actually
    // * necessary. Spring is smart enough to pull the correct DataSource bean. That said, I think it reads better to call it out.
    @Bean("test-postgres-datasource")
    fun testPostgresDataSource(): DataSource {
        val containerConfig = startTestContainer(ContainerConfiguration("postgres", "password"))
        val dataSource = configureDataSource(containerConfig)

        return dataSource
    }

    @Bean
    fun namedParameterJdbcTemplate(@Qualifier("test-postgres-datasource") dataSource: DataSource): NamedParameterJdbcTemplate {
        return NamedParameterJdbcTemplate(dataSource)
    }

    companion object {
        fun startTestContainer(containerConfiguration: ContainerConfiguration): ContainerConfiguration {
            val postgresContainer =
                PostgreSQLContainer("postgres:15-alpine")
                    .withUsername("postgres")
                    .withPassword("password")

            postgresContainer.start()

            return containerConfiguration.copy(
                host = postgresContainer.host,
                port = postgresContainer.firstMappedPort
            )
        }

        fun configureDataSource(containerConfiguration: ContainerConfiguration): DataSource {
            return DataSourceBuilder
                .create()
                .apply {
                    driverClassName("org.postgresql.Driver")
                    url("jdbc:postgresql://${containerConfiguration.urlFormat}/postgres")
                    username(containerConfiguration.username)
                    password(containerConfiguration.password)
                }
                .build()
        }
    }
}


data class ContainerConfiguration(
    val username: String,
    val password: String,
    val host: String? = null,
    val port: Int? = null
) {
    val urlFormat: String
        get() = "$host:$port"
}
