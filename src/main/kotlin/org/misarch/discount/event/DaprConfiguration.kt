package org.misarch.discount.event

import io.dapr.client.DaprClient
import io.dapr.client.DaprClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for Dapr
 */
@Configuration
class DaprConfiguration {

    /**
     * Creates a Dapr client
     *
     * @return the created Dapr client
     */
    @Bean
    suspend fun daprClient(): DaprClient {
        return DaprClientBuilder().build()
    }

}