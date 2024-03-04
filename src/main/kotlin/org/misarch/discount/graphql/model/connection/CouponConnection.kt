package org.misarch.discount.graphql.model.connection

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ShareableDirective
import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.sql.SQLQuery
import org.misarch.discount.graphql.AuthorizedUser
import org.misarch.discount.graphql.model.Coupon
import org.misarch.discount.graphql.model.connection.base.*
import org.misarch.discount.persistence.model.CouponEntity
import org.misarch.discount.persistence.repository.CouponRepository

/**
 * A GraphQL connection for [Coupon]s.
 *
 * @param first The maximum number of items to return
 * @param skip The number of items to skip
 * @param predicate The predicate to filter the items by
 * @param order The order to sort the items by
 * @param repository The repository to fetch the items from
 * @param authorizedUser The authorized user
 * @param applyJoin A function to apply a join to the query
 */
@GraphQLDescription("A connection to a list of `Coupon` values.")
@ShareableDirective
class CouponConnection(
    first: Int?,
    skip: Int?,
    predicate: BooleanExpression?,
    order: CouponOrder?,
    repository: CouponRepository,
    authorizedUser: AuthorizedUser?,
    applyJoin: (query: SQLQuery<*>) -> SQLQuery<*> = { it }
) : BaseConnection<Coupon, CouponEntity>(
    first,
    skip,
    null,
    predicate,
    (order ?: CouponOrder.DEFAULT).toOrderSpecifier(CouponOrderField.ID),
    repository,
    CouponEntity.ENTITY,
    authorizedUser,
    applyJoin
) {

    override val primaryKey: ComparableExpression<*> get() = CouponEntity.ENTITY.id

    @GraphQLDescription("The resulting items.")
    override suspend fun nodes(): List<Coupon> {
        return super.nodes().map { it }
    }

}

@GraphQLDescription("Coupon order fields")
enum class CouponOrderField(override vararg val expressions: Expression<out Comparable<*>>) : BaseOrderField {
    @GraphQLDescription("Order coupons by their id")
    ID(CouponEntity.ENTITY.id),
}

@GraphQLDescription("Coupon order")
class CouponOrder(
    direction: OrderDirection?, field: CouponOrderField?
) : BaseOrder<CouponOrderField>(direction, field) {

    companion object {
        val DEFAULT = CouponOrder(OrderDirection.ASC, CouponOrderField.ID)
    }
}