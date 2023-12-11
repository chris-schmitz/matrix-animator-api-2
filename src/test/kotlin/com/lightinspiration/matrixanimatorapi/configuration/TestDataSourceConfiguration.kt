package com.lightinspiration.matrixanimatorapi.configuration

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.testcontainers.containers.PostgreSQLContainer
import javax.sql.DataSource

@Configuration
@Profile("integration-test")
class TestDataSourceConfiguration {

    @Bean("test-postgres-datasource")
    fun testPostgresDataSource(): DataSource {
        val postgresContainer =
            PostgreSQLContainer("postgres:15-alpine")
                .withUsername("postgres")
                .withPassword("password")
        // TODO: search and maybe ask andrew
        // ? this is great, but how do we stop the container when we're done????????
        postgresContainer.start()
        // TODO: get the port and then use that instead of hardcoded number in the url
        val host = postgresContainer.host
        val port = postgresContainer.firstMappedPort
        print("\n----> test db host: $host")
        print("\n----> test db port: $port")
        print("\n")


        return DataSourceBuilder
            .create()
            .apply {
                driverClassName("org.postgresql.Driver")
                url("jdbc:postgresql://$host:$port/postgres")
                username("postgres")
                password("password")
            }
            .build()
    }

}

