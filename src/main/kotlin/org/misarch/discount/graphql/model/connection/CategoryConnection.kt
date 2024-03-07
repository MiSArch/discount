package org.misarch.discount.graphql.model.connection

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ShareableDirective
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.sql.SQLQuery
import org.misarch.discount.graphql.AuthorizedUser
import org.misarch.discount.graphql.model.Category
import org.misarch.discount.graphql.model.connection.base.BaseConnection
import org.misarch.discount.persistence.model.CategoryEntity
import org.misarch.discount.persistence.repository.CategoryRepository
import org.misarch.discount.graphql.model.connection.base.CommonOrder

/**
 * A GraphQL connection for [Category]s.
 *
 * @param first The maximum number of items to return
 * @param skip The number of items to skip
 * @param predicate The predicate to filter the items by
 * @param order The order to sort the items by
 * @param repository The repository to fetch the items from
 * @param authorizedUser The authorized user
 * @param applyJoin A function to apply a join to the query
 */
@GraphQLDescription("A connection to a list of `Category` values.")
@ShareableDirective
class CategoryConnection(
    first: Int?,
    skip: Int?,
    predicate: BooleanExpression?,
    order: CommonOrder?,
    repository: CategoryRepository,
    authorizedUser: AuthorizedUser?,
    applyJoin: (query: SQLQuery<*>) -> SQLQuery<*> = { it }
) : BaseConnection<Category, CategoryEntity>(
    first,
    skip,
    null,
    predicate,
    (order ?: CommonOrder.DEFAULT).toOrderSpecifier(CategoryEntity.ENTITY.id),
    repository,
    CategoryEntity.ENTITY,
    authorizedUser,
    applyJoin
) {

    override val primaryKey: ComparableExpression<*> get() = CategoryEntity.ENTITY.id

}