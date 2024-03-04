package org.misarch.discount.graphql.model.connection

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ShareableDirective
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.sql.SQLQuery
import org.misarch.discount.graphql.AuthorizedUser
import org.misarch.discount.graphql.model.User
import org.misarch.discount.graphql.model.connection.base.BaseConnection
import org.misarch.discount.graphql.model.connection.base.CommonOrder
import org.misarch.discount.persistence.model.UserEntity
import org.misarch.discount.persistence.repository.UserRepository

/**
 * A GraphQL connection for [User]s.
 *
 * @param first The maximum number of items to return
 * @param skip The number of items to skip
 * @param predicate The predicate to filter the items by
 * @param order The order to sort the items by
 * @param repository The repository to fetch the items from
 * @param authorizedUser The authorized user
 * @param applyJoin A function to apply a join to the query
 */
@GraphQLDescription("A connection to a list of `User` values.")
@ShareableDirective
class UserConnection(
    first: Int?,
    skip: Int?,
    predicate: BooleanExpression?,
    order: CommonOrder?,
    repository: UserRepository,
    authorizedUser: AuthorizedUser?,
    applyJoin: (query: SQLQuery<*>) -> SQLQuery<*> = { it }
) : BaseConnection<User, UserEntity>(
    first,
    skip,
    null,
    predicate,
    (order ?: CommonOrder.DEFAULT).toOrderSpecifier(UserEntity.ENTITY.id),
    repository,
    UserEntity.ENTITY,
    authorizedUser,
    applyJoin
) {

    override val primaryKey: ComparableExpression<*> get() = UserEntity.ENTITY.id

}