package org.misarch.discount.graphql.model.connection.base

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.querydsl.core.types.Expression
import com.querydsl.core.types.OrderSpecifier

@GraphQLDescription("Discount order")
class CommonOrder(
    @GraphQLDescription("The direction to order by")
    val direction: OrderDirection?,
    @GraphQLDescription("The field to order by")
    val field: CommonOrderField?
) {
    companion object {
        val DEFAULT = CommonOrder(OrderDirection.ASC, CommonOrderField.ID)
    }

    /**
     * Convert this order to a QueryDSL order specifier
     *
     * @param expression The expression to order by (should be id)
     */
    fun toOrderSpecifier(expression: Expression<out Comparable<*>>): Array<OrderSpecifier<*>> {
        return arrayOf(OrderSpecifier((direction ?: OrderDirection.ASC).direction, expression))
    }

}