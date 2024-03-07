package org.misarch.discount.graphql.model.connection.base

import com.querydsl.core.types.Expression

/**
 * A GraphQL order field
 */
interface BaseOrderField {
    /**
     * The expressions to order by
     */
    val expressions: Array<out Expression<out Comparable<*>>>
}