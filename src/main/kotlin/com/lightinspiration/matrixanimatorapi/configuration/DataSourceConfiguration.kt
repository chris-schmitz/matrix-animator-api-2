package com.lightinspiration.matrixanimatorapi.configuration

import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.Scope
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.DirectoryResourceAccessor
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.io.File
import javax.sql.DataSource

@Configuration("src-datasource-configuration")
@Profile("!integration-test")
class DataSourceConfiguration {

    // TODO: review  https://github.com/FoodEssentials/example-service-multi-module-api/blob/46d36898ba0e891b0753f01032e91989a4f6c5fe/example-service-multi-module-api/src/main/kotlin/com/labelinsight/example/configuration/DatabaseConfiguration.kt#L17-L21
    // * The datasource defines the information needed to connect to a Database to access the various schemas->tables->records inside
    // * our queries from the template level happen within a given database but need to specify the target data starting at the schema level
    @Primary
    @Bean("Postgres DataSource")
    @ConfigurationProperties("spring.datasource.schmitz-sandbox")
    fun postgresDataSource(): DataSource {
        val dataSource = DataSourceBuilder
            .create()
            .build()


        return dataSource
    }

    @Bean("src-named-parameter-jdbc-template")
    fun namedParameterJdbcTemplate(dataSource: DataSource): NamedParameterJdbcTemplate {
        return NamedParameterJdbcTemplate(dataSource)
    }

    private fun buildSchema(
        dataSource: DataSource
    ) {
        Scope.child(
            emptyMap()
        ) { ->
            // TODO: the path should prob be abstracted and passed in as a property
            //val changeLog = File("db/changelog/db.changelog-master.yaml")
            val changeLog = File(javaClass.classLoader.getResource("db/changelog/db.changelog-master.yaml").file)
            print("\n")
            print("\n")
            print("\n")
            print("\n")
            print("====================> $changeLog\n")
            print(changeLog.parentFile.listFiles())
            print("\n")
            print("\n")
            print("\n")
            val liquibase = Liquibase(
                changeLog.name,
                DirectoryResourceAccessor(changeLog.parentFile),
                DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(JdbcConnection(dataSource.connection))
            )
            liquibase.update(Contexts(), LabelExpression())
        }
    }

}