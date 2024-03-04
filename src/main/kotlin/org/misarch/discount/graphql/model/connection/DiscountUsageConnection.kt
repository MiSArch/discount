package org.misarch.discount.graphql.model.connection

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ShareableDirective
import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.sql.SQLQuery
import org.misarch.discount.graphql.AuthorizedUser
import org.misarch.discount.graphql.model.DiscountUsage
import org.misarch.discount.graphql.model.connection.base.*
import org.misarch.discount.persistence.model.DiscountUsageEntity
import org.misarch.discount.persistence.repository.DiscountUsageRepository

/**
 * A GraphQL connection for [DiscountUsage]s.
 *
 * @param first The maximum number of items to return
 * @param skip The number of items to skip
 * @param predicate The predicate to filter the items by
 * @param order The order to sort the items by
 * @param repository The repository to fetch the items from
 * @param authorizedUser The authorized user
 * @param applyJoin A function to apply a join to the query
 */
@GraphQLDescription("A connection to a list of `DiscountUsage` values.")
@ShareableDirective
class DiscountUsageConnection(
    first: Int?,
    skip: Int?,
    predicate: BooleanExpression?,
    order: DiscountUsageOrder?,
    repository: DiscountUsageRepository,
    authorizedUser: AuthorizedUser?,
    applyJoin: (query: SQLQuery<*>) -> SQLQuery<*> = { it }
) : BaseConnection<DiscountUsage, DiscountUsageEntity>(
    first,
    skip,
    null,
    predicate,
    (order ?: DiscountUsageOrder.DEFAULT).toOrderSpecifier(DiscountUsageOrderField.ID),
    repository,
    DiscountUsageEntity.ENTITY,
    authorizedUser,
    applyJoin
) {

    override val primaryKey: ComparableExpression<*> get() = DiscountUsageEntity.ENTITY.id

    @GraphQLDescription("The resulting items.")
    override suspend fun nodes(): List<DiscountUsage> {
        return super.nodes().map { it }
    }

}

@GraphQLDescription("DiscountUsage order fields")
enum class DiscountUsageOrderField(override vararg val expressions: Expression<out Comparable<*>>) : BaseOrderField {
    @GraphQLDescription("Order discount usages by their id")
    ID(DiscountUsageEntity.ENTITY.id),

    @GraphQLDescription("Order discount usages by their usages")
    USAGES(DiscountUsageEntity.ENTITY.usages, DiscountUsageEntity.ENTITY.id),
}

@GraphQLDescription("DiscountUsage order")
class DiscountUsageOrder(
    direction: OrderDirection?, field: DiscountUsageOrderField?
) : BaseOrder<DiscountUsageOrderField>(direction, field) {

    companion object {
        val DEFAULT = DiscountUsageOrder(OrderDirection.ASC, DiscountUsageOrderField.ID)
    }
}