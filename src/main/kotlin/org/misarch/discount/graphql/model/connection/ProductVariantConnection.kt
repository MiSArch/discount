package org.misarch.discount.graphql.model.connection

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.ShareableDirective
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.ComparableExpression
import com.querydsl.sql.SQLQuery
import org.misarch.discount.graphql.AuthorizedUser
import org.misarch.discount.graphql.model.ProductVariant
import org.misarch.discount.graphql.model.connection.base.BaseConnection
import org.misarch.discount.graphql.model.connection.base.CommonOrder
import org.misarch.discount.persistence.model.ProductVariantEntity
import org.misarch.discount.persistence.repository.ProductVariantRepository

/**
 * A GraphQL connection for [ProductVariant]s.
 *
 * @param first The maximum number of items to return
 * @param skip The number of items to skip
 * @param predicate The predicate to filter the items by
 * @param order The order to sort the items by
 * @param repository The repository to fetch the items from
 * @param authorizedUser The authorized user
 * @param applyJoin A function to apply a join to the query
 */
@GraphQLDescription("A connection to a list of `ProductVariant` values.")
@ShareableDirective
class ProductVariantConnection(
    first: Int?,
    skip: Int?,
    predicate: BooleanExpression?,
    order: CommonOrder?,
    repository: ProductVariantRepository,
    authorizedUser: AuthorizedUser?,
    applyJoin: (query: SQLQuery<*>) -> SQLQuery<*> = { it }
) : BaseConnection<ProductVariant, ProductVariantEntity>(
    first,
    skip,
    null,
    predicate,
    (order ?: CommonOrder.DEFAULT).toOrderSpecifier(ProductVariantEntity.ENTITY.id),
    repository,
    ProductVariantEntity.ENTITY,
    authorizedUser,
    applyJoin
) {

    override val primaryKey: ComparableExpression<*> get() = ProductVariantEntity.ENTITY.id

    @GraphQLDescription("The resulting items.")
    override suspend fun nodes(): List<ProductVariant> {
        return super.nodes().map { it }
    }

}