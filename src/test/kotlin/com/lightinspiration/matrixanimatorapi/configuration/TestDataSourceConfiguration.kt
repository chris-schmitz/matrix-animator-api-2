package com.lightinspiration.matrixanimatorapi.configuration

import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.FileSystemResourceAccessor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer
import java.io.File
import javax.sql.DataSource

@Configuration("test-datasource-configuration")
@Profile("integration-test")
class TestDataSourceConfiguration {

    @Primary
    @Bean("test-postgres-datasource")
    fun testPostgresDataSource(): DataSource {
        val postgresContainer =
            PostgreSQLContainer("postgres:15-alpine")
                .withUsername("postgres")
                .withPassword("password")

        postgresContainer.start()

        val host = postgresContainer.host
        val port = postgresContainer.firstMappedPort

        val dataSource = DataSourceBuilder
            .create()
            .apply {
                driverClassName("org.postgresql.Driver")
                url("jdbc:postgresql://$host:$port/postgres")
                username("postgres")
                password("password")
            }
            .build()

        val changeLog = File("src/test/resources/db/changelog/db.changelog-master.yaml")
        // TODO: this works, but it's using deprecated tooling. figure out the current way of doing it
        val liquibase = Liquibase(
            changeLog.name,
            FileSystemResourceAccessor(changeLog.parentFile),
            DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection))
        )
        liquibase.update(Contexts(), LabelExpression())

        return dataSource
    }

    @Bean
    @Primary
    fun namedParameterJdbcTemplate(@Qualifier("test-postgres-datasource") dataSource: DataSource): NamedParameterJdbcTemplate {
        return NamedParameterJdbcTemplate(dataSource)
    }
}

