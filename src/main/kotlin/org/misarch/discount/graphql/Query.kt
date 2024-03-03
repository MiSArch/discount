package org.misarch.discount.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.dataloader.CouponDataLoader
import org.misarch.discount.graphql.dataloader.DiscountDataLoader
import org.misarch.discount.graphql.model.Coupon
import org.misarch.discount.graphql.model.Discount
import org.misarch.discount.persistence.repository.CouponRepository
import org.misarch.discount.persistence.repository.DiscountRepository
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Defines GraphQL queries
 *
 * @property discountRepository repository for discounts
 * @property couponRepository repository for coupons
 */
@Component
class Query(
    private val discountRepository: DiscountRepository,
    private val couponRepository: CouponRepository
) : Query {

    @GraphQLDescription("Get a discount by id")
    fun discount(
        @GraphQLDescription("The id of the discount")
        id: UUID,
        dfe: DataFetchingEnvironment
    ): CompletableFuture<Discount> {
        dfe.authorizedUser.checkIsEmployee()
        return dfe.getDataLoader<UUID, Discount>(DiscountDataLoader::class.simpleName!!).load(id)
    }

    @GraphQLDescription("Get a coupon by id")
    fun coupon(
        @GraphQLDescription("The id of the coupon")
        id: UUID,
        dfe: DataFetchingEnvironment
    ): CompletableFuture<Coupon> {
        dfe.authorizedUser.checkIsEmployee()
        return dfe.getDataLoader<UUID, Coupon>(CouponDataLoader::class.simpleName!!).load(id)
    }
}