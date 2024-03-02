package org.misarch.discount.graphql.model.connection

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ShareableDirective
import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.sql.SQLQuery
import org.misarch.discount.graphql.AuthorizedUser
import org.misarch.discount.graphql.model.Discount
import org.misarch.discount.graphql.model.UserAddress
import org.misarch.discount.graphql.model.connection.base.*
import org.misarch.discount.persistence.model.DiscountEntity
import org.misarch.discount.persistence.repository.AddressRepository

/**
 * A GraphQL connection for [UserAddress]s.
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
@GraphQLDescription("A connection to a list of `Address` values.")
@ShareableDirective
class UserAddressConnection(
    first: Int?,
    skip: Int?,
    filter: UserAddressFilter?,
    predicate: BooleanExpression?,
    order: UserAddressOrder?,
    repository: AddressRepository,
    authorizedUser: AuthorizedUser?,
    applyJoin: (query: SQLQuery<*>) -> SQLQuery<*> = { it }
) : BaseConnection<Discount, DiscountEntity>(
    first,
    skip,
    filter,
    predicate,
    (order ?: UserAddressOrder.DEFAULT).toOrderSpecifier(UserAddressOrderField.ID),
    repository,
    DiscountEntity.ENTITY,
    authorizedUser,
    applyJoin
) {

    override val primaryKey: ComparableExpression<*> get() = DiscountEntity.ENTITY.id

    @GraphQLDescription("The resulting items.")
    override suspend fun nodes(): List<UserAddress> {
        return super.nodes().map { it as UserAddress }
    }

}

@GraphQLDescription("User address order fields")
enum class UserAddressOrderField(override vararg val expressions: Expression<out Comparable<*>>) : BaseOrderField {
    @GraphQLDescription("Order addresss by their id")
    ID(DiscountEntity.ENTITY.id),


}

@GraphQLDescription("User address order")
class UserAddressOrder(
    direction: OrderDirection?, field: UserAddressOrderField?
) : BaseOrder<UserAddressOrderField>(direction, field) {

    companion object {
        val DEFAULT = UserAddressOrder(OrderDirection.ASC, UserAddressOrderField.ID)
    }
}

@GraphQLDescription("User address filter")
class UserAddressFilter(
    val isArchived: Boolean?
) : BaseFilter {

    override fun toExpression(): BooleanExpression? {
        return if (isArchived != null) {
            DiscountEntity.ENTITY.archivedAt.let {
                if (isArchived) it.isNotNull else it.isNull
            }
        } else {
            null
        }
    }

}