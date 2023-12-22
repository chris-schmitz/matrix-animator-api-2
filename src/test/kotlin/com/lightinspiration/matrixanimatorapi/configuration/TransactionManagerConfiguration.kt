package com.lightinspiration.matrixanimatorapi.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@Profile("integration-test")
class TransactionManagerConfiguration {

    @Bean
    fun transactionManager(@Qualifier("test-postgres-datasource") dataSource: DataSource): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource)
    }
}