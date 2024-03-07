package org.misarch.discount.graphql.model.connection.base

import com.querydsl.core.types.dsl.BooleanExpression
import org.misarch.discount.graphql.AuthorizedUser

/**
 * Base class for all filter inputs
 */
interface BaseFilter {

    /**
     * Convert the filter to a QueryDSL expression
     *
     * @param authorizedUser The authorized user
     * @return The QueryDSL expression, or null if the filter is empty
     */
    fun toExpression(authorizedUser: AuthorizedUser?): BooleanExpression?

}