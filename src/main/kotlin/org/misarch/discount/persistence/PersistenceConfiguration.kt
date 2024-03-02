package org.misarch.discount.persistence

import com.querydsl.sql.PostgreSQLTemplates
import io.r2dbc.spi.ConnectionFactory
import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.flyway.FlywayProperties
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.connection.R2dbcTransactionManager


/**
 * Database / persistence configuration
 * Configures Flyway migrations and reactive transactions
 */
@Configuration
@EnableConfigurationProperties(R2dbcProperties::class, FlywayProperties::class)
internal class PersistenceConfiguration {

    /**
     * Configures Flyway migrations
     *
     * @param flywayProperties the provided Flyway properties
     * @param r2dbcProperties the provided R2DBC properties
     * @return the configured Flyway instance
     */
    @Bean(initMethod = "migrate")
    @ConditionalOnProperty(name = ["spring.flyway.enabled"], matchIfMissing = true, havingValue = "true")
    fun flyway(flywayProperties: FlywayProperties, r2dbcProperties: R2dbcProperties): Flyway {
        return Flyway.configure()
            .dataSource(
                flywayProperties.url,
                r2dbcProperties.username,
                r2dbcProperties.password
            )
            .locations(*flywayProperties.locations.toTypedArray())
            .baselineOnMigrate(true)
            .load()
    }

    /**
     * Configures reactive transactions via a [R2dbcTransactionManager]
     *
     * @param connectionFactory the provided connection factory
     * @return the configured transaction manager
     */
    @Bean
    fun transactionManager(connectionFactory: ConnectionFactory): R2dbcTransactionManager {
        return R2dbcTransactionManager(connectionFactory)
    }

    /**
     * Configures Querydsl SQL templates
     * Overwrites the default configuration which depends on flyway
     *
     * @return the configured SQL templates
     */
    @Bean
    fun querydslSqlConfiguration(): com.querydsl.sql.Configuration {
        val configuration = com.querydsl.sql.Configuration(PostgreSQLTemplates.DEFAULT)
        return configuration
    }
}