package org.misarch.discount.graphql.model.connection

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ShareableDirective
import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.sql.SQLExpressions
import com.querydsl.sql.SQLQuery
import org.misarch.discount.graphql.AuthorizedUser
import org.misarch.discount.graphql.model.Coupon
import org.misarch.discount.graphql.model.connection.base.*
import org.misarch.discount.persistence.model.CouponEntity
import org.misarch.discount.persistence.model.CouponRedemptionEntity
import org.misarch.discount.persistence.repository.CouponRepository
import java.util.UUID

/**
 * A GraphQL connection for [Coupon]s.
 *
 * @param first The maximum number of items to return
 * @param skip The number of items to skip
 * @param filter The filter to apply to the items
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
    filter: CouponFilter?,
    predicate: BooleanExpression?,
    order: CouponOrder?,
    repository: CouponRepository,
    authorizedUser: AuthorizedUser?,
    applyJoin: (query: SQLQuery<*>) -> SQLQuery<*> = { it }
) : BaseConnection<Coupon, CouponEntity>(
    first,
    skip,
    filter,
    predicate,
    (order ?: CouponOrder.DEFAULT).toOrderSpecifier(CouponOrderField.ID),
    repository,
    CouponEntity.ENTITY,
    authorizedUser,
    applyJoin
) {

    override val primaryKey: ComparableExpression<*> get() = CouponEntity.ENTITY.id

    override fun authorizedUserFilter(): BooleanExpression? {
        return if (authorizedUser == null) {
            Expressions.FALSE
        } else if (authorizedUser.isEmployee) {
            null
        } else {
            userHasCouponCondition(authorizedUser.id)
        }
    }

}

@GraphQLDescription("Coupon order fields")
enum class CouponOrderField(override vararg val expressions: Expression<out Comparable<*>>) : BaseOrderField {
    @GraphQLDescription("Order coupons by their id")
    ID(CouponEntity.ENTITY.id),

    @GraphQLDescription("Order coupons by the valid from date")
    VALID_FROM(CouponEntity.ENTITY.validFrom, CouponEntity.ENTITY.id),

    @GraphQLDescription("Order coupons by the valid until date")
    VALID_UNTIL(CouponEntity.ENTITY.validUntil, CouponEntity.ENTITY.id),
}

@GraphQLDescription("Coupon order")
class CouponOrder(
    direction: OrderDirection?, field: CouponOrderField?
) : BaseOrder<CouponOrderField>(direction, field) {

    companion object {
        val DEFAULT = CouponOrder(OrderDirection.ASC, CouponOrderField.ID)
    }
}

@GraphQLDescription("Coupon filter")
class CouponFilter(
    @property:GraphQLDescription("Filter weather the user with the provided id own the coupon, other users than the authenticated user require at least EMPLOYEE")
    val userHasCoupon: UUID?
) : BaseFilter {

    override fun toExpression(authorizedUser: AuthorizedUser?): BooleanExpression? {
        return if (userHasCoupon != null) {
            require(authorizedUser != null) {
                "The userHasCoupon filter requires an authorized user"
            }
            if (userHasCoupon != authorizedUser.id) {
                authorizedUser.checkIsEmployee()
            }
            userHasCouponCondition(userHasCoupon)
        } else {
            null
        }
    }
}

/**
 * A function to filter the coupons by the user that owns them.
 *
 * @param userId The user id
 * @return The predicate to filter the coupons by
 */
private fun userHasCouponCondition(userId: UUID): BooleanExpression {
    return CouponEntity.ENTITY.id.`in`(
        SQLExpressions.select(CouponRedemptionEntity.ENTITY.couponId).from(CouponRedemptionEntity.ENTITY)
            .where(CouponRedemptionEntity.ENTITY.userId.eq(userId))
    )
}