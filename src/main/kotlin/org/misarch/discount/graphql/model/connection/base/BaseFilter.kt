package org.misarch.discount.graphql.model.connection.base

import com.querydsl.core.types.dsl.BooleanExpression

/**
 * Base class for all filter inputs
 */
interface BaseFilter {

    /**
     * Convert the filter to a QueryDSL expression
     *
     * @return The QueryDSL expression, or null if the filter is empty
     */
    fun toExpression(): BooleanExpression?

}