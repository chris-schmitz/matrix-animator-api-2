package com.lightinspiration.matrixanimatorapi.configuration

import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.Scope
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.DirectoryResourceAccessor
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.testcontainers.containers.PostgreSQLContainer
import java.io.File
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

        // * keeping this around for now. I have a feeling there's a chance this may be needed once the
        // * db creds for prod and test are different, but really that _should_ be solve-able via test properties.
        fun buildSchema(dataSource: DataSource) {
            Scope.child(
                emptyMap()
            ) { ->
                // TODO: the path should prob be abstracted and passed in as a property
                val changeLog = File("src/main/resources/db/changelog/db.changelog-master.yaml")
                val liquibase = Liquibase(
                    changeLog.name,
                    DirectoryResourceAccessor(changeLog.parentFile),
                    DatabaseFactory.getInstance()
                        .findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection))
                )
                liquibase.update(Contexts(), LabelExpression())
                // * From what I'm reading this _should_ be the non-deprecated approach to starting and running liquibase,
                // * but it doesn't work and I'm tired of fiddling with it :|
                //val database =
                //    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection))
                //CommandScope(UpdateCommandStep.COMMAND_NAME.first())
                //    .addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database)
                //    .addArgumentValue(
                //        UpdateCommandStep.CHANGELOG_FILE_ARG,
                //        "src/test/resources/db/changelog/db.changelog-master.yaml"
                //    )
                //    .execute()
            }
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
