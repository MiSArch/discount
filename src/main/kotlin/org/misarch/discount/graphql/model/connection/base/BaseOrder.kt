package org.misarch.discount.graphql.model.connection.base

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.querydsl.core.types.OrderSpecifier

/**
 * A GraphQL order, consisting of a field and a direction
 *
 * @param T The type of the field
 * @property direction The direction to order by
 * @property field The field to order by
 */
@GraphQLIgnore
abstract class BaseOrder<T : BaseOrderField>(
    @property:GraphQLDescription("The direction to order by")
    val direction: OrderDirection?,
    @property:GraphQLDescription("The field to order by")
    val field: T?
) {

    /**
     * Convert this order to a QueryDSL order specifier
     */
    fun toOrderSpecifier(defaultField: T): Array<OrderSpecifier<*>> {
        return (field ?: defaultField).expressions.map { OrderSpecifier((direction ?: OrderDirection.ASC).direction, it) }.toTypedArray()
    }

}