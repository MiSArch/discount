package org.misarch.discount.graphql.model.connection

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ShareableDirective
import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.sql.SQLQuery
import org.misarch.discount.graphql.AuthorizedUser
import org.misarch.discount.graphql.model.Discount
import org.misarch.discount.graphql.model.connection.base.*
import org.misarch.discount.persistence.model.DiscountEntity
import org.misarch.discount.persistence.repository.DiscountRepository

/**
 * A GraphQL connection for [Discount]s.
 *
 * @param first The maximum number of items to return
 * @param skip The number of items to skip
 * @param predicate The predicate to filter the items by
 * @param order The order to sort the items by
 * @param repository The repository to fetch the items from
 * @param authorizedUser The authorized user
 * @param applyJoin A function to apply a join to the query
 */
@GraphQLDescription("A connection to a list of `Discount` values.")
@ShareableDirective
class DiscountConnection(
    first: Int?,
    skip: Int?,
    predicate: BooleanExpression?,
    order: DiscountOrder?,
    repository: DiscountRepository,
    authorizedUser: AuthorizedUser?,
    applyJoin: (query: SQLQuery<*>) -> SQLQuery<*> = { it }
) : BaseConnection<Discount, DiscountEntity>(
    first,
    skip,
    null,
    predicate,
    (order ?: DiscountOrder.DEFAULT).toOrderSpecifier(DiscountOrderField.ID),
    repository,
    DiscountEntity.ENTITY,
    authorizedUser,
    applyJoin
) {

    override val primaryKey: ComparableExpression<*> get() = DiscountEntity.ENTITY.id

    @GraphQLDescription("The resulting items.")
    override suspend fun nodes(): List<Discount> {
        return super.nodes().map { it as Discount }
    }

}

@GraphQLDescription("Discount order fields")
enum class DiscountOrderField(override vararg val expressions: Expression<out Comparable<*>>) : BaseOrderField {
    @GraphQLDescription("Order discounts by their id")
    ID(DiscountEntity.ENTITY.id),


}

@GraphQLDescription("Discount order")
class DiscountOrder(
    direction: OrderDirection?, field: DiscountOrderField?
) : BaseOrder<DiscountOrderField>(direction, field) {

    companion object {
        val DEFAULT = DiscountOrder(OrderDirection.ASC, DiscountOrderField.ID)
    }
}