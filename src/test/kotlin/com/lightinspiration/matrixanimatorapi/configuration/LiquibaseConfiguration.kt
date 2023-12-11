package com.lightinspiration.matrixanimatorapi.configuration

import liquibase.integration.spring.SpringLiquibase
import org.springframework.beans.factory.annotation.Qualifier
import javax.sql.DataSource

// TODO: delete if it's not needed
//@Configuration
class LiquibaseConfiguration {

    //@Bean
    fun liquibase(@Qualifier("test-postgres-datasource") datasource: DataSource): SpringLiquibase {
        val liquibase = SpringLiquibase()
        liquibase.changeLog = "classpath:resources/database/changelog.json"
        liquibase.dataSource = datasource
        return liquibase
    }
}