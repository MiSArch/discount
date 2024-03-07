package org.misarch.discount.graphql

import com.expediagroup.graphql.generator.extensions.plus
import com.expediagroup.graphql.server.spring.execution.DefaultSpringGraphQLContextFactory
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.GraphQLContext
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest

/**
 * Factory that creates a new instance of [GraphQLContext] per request.
 *
 * @param objectMapper jackson object mapper used to serialize/deserialize the context
 */
@Component
class AuthorizationGraphQLContextFactory(
    private val objectMapper: ObjectMapper
) : DefaultSpringGraphQLContextFactory() {

    override suspend fun generateContext(request: ServerRequest): GraphQLContext {
        val userHeader = request.headers().firstHeader("Authorized-User")
        val additionalContext = if (userHeader != null) {
            mapOf(AuthorizedUser::class to objectMapper.readValue(userHeader, AuthorizedUser::class.java))
        } else {
            emptyMap()
        }
        return super.generateContext(request) + additionalContext
    }
}