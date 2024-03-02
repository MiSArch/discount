package org.misarch.discount.graphql.model.connection.base

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.querydsl.core.types.Order

/**
 * GraphQL order direction
 */
@GraphQLDescription("Order direction")
enum class OrderDirection(internal val direction: Order) {
    @GraphQLDescription("Ascending order")
    ASC(Order.ASC),

    @GraphQLDescription("Descending order")
    DESC(Order.DESC);
}