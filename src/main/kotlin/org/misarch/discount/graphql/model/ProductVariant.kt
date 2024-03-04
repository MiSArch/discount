package org.misarch.discount.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.sql.SQLExpressions
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.authorizedUser
import org.misarch.discount.graphql.authorizedUserOrNull
import org.misarch.discount.graphql.model.connection.DiscountConnection
import org.misarch.discount.graphql.model.connection.DiscountOrder
import org.misarch.discount.persistence.model.*
import org.misarch.discount.persistence.repository.DiscountRepository
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@GraphQLDescription("A product variant")
class ProductVariant(
    id: UUID
) : Node(id) {

    @GraphQLDescription("Get all discounts which apply directly to this product variant")
    fun discounts(
        @GraphQLDescription("Number of items to return")
        first: Int? = null,
        @GraphQLDescription("Number of items to skip")
        skip: Int? = null,
        @GraphQLDescription("Ordering")
        orderBy: DiscountOrder? = null,
        @GraphQLIgnore
        @Autowired
        discountRepository: DiscountRepository, dfe: DataFetchingEnvironment
    ): DiscountConnection {
        dfe.authorizedUser.checkIsEmployee()
        return DiscountConnection(
            first,
            skip,
            DiscountToProductVariantEntity.ENTITY.productVariantId.eq(id),
            orderBy,
            discountRepository,
            dfe.authorizedUserOrNull
        ) {
            it.innerJoin(DiscountToProductVariantEntity.ENTITY)
                .on(DiscountToProductVariantEntity.ENTITY.discountId.eq(DiscountEntity.ENTITY.id))
        }
    }

    @GraphQLDescription("Get all discounts which apply to this product variant for the authenticated user")
    fun applicableDiscounts(
        @GraphQLDescription("Number of items to return")
        first: Int? = null,
        @GraphQLDescription("Number of items to skip")
        skip: Int? = null,
        @GraphQLDescription("Ordering")
        orderBy: DiscountOrder? = null,
        @GraphQLIgnore
        @Autowired
        discountRepository: DiscountRepository, dfe: DataFetchingEnvironment
    ): DiscountConnection {
        val discountAppliesCondition = generateDiscountAppliesCondition(dfe)
        return DiscountConnection(
            first, skip, discountAppliesCondition, orderBy, discountRepository, dfe.authorizedUserOrNull
        )
    }

    /**
     * Generates a condition that checks that a discount either applies to the product variant, the owning product,
     * or any of the categories of the product.
     * Also checks that either no coupons are required, or the user has a coupon for the discount.
     *
     * @param dfe The data fetching environment, used to get the authorized user
     * @return The condition
     */
    private fun generateDiscountAppliesCondition(dfe: DataFetchingEnvironment): BooleanExpression? {
        val appliesCondition = generateDiscountAppliesCondition()
        val hasNoRequiredCouponsCondition = SQLExpressions.select(Expressions.TRUE).from(CouponEntity.ENTITY)
            .where(CouponEntity.ENTITY.discountId.eq(DiscountEntity.ENTITY.id)).notExists()
        val authorizedUser = dfe.authorizedUserOrNull
        val couponsCondition = if (authorizedUser == null) {
            hasNoRequiredCouponsCondition
        } else {
            val userHasCouponCondition =
                SQLExpressions.select(Expressions.TRUE).from(CouponEntity.ENTITY).join(CouponToUserEntity.ENTITY)
                    .on(CouponEntity.ENTITY.id.eq(CouponToUserEntity.ENTITY.couponId)).where(
                        CouponEntity.ENTITY.discountId.eq(DiscountEntity.ENTITY.id)
                            .and(CouponToUserEntity.ENTITY.userId.eq(authorizedUser.id))
                    ).exists()
            hasNoRequiredCouponsCondition.or(userHasCouponCondition)
        }
        val discountAppliesCondition = appliesCondition.and(couponsCondition)
        return discountAppliesCondition
    }

    /**
     * Generates a condition that checks that a discount either applies to the product variant, the owning product,
     * or any of the categories of the product.
     * Does NOT check for required coupons.
     *
     * @return The condition
     */
    private fun generateDiscountAppliesCondition(): BooleanExpression {
        val appliesProductVariantCondition = DiscountEntity.ENTITY.id.`in`(
            SQLExpressions.select(DiscountToProductVariantEntity.ENTITY.discountId)
                .from(DiscountToProductVariantEntity.ENTITY)
                .where(DiscountToProductVariantEntity.ENTITY.productVariantId.eq(id))
        )
        val appliesProductCondition = DiscountEntity.ENTITY.id.`in`(
            SQLExpressions.select(DiscountToProductEntity.ENTITY.discountId).from(DiscountToProductEntity.ENTITY)
                .join(ProductVariantEntity.ENTITY)
                .on(DiscountToProductEntity.ENTITY.productId.eq(ProductVariantEntity.ENTITY.productId))
                .where(ProductVariantEntity.ENTITY.id.eq(id))
        )
        val appliesCategoryCondition = DiscountEntity.ENTITY.id.`in`(
            SQLExpressions.select(DiscountToCategoryEntity.ENTITY.discountId).from(DiscountToCategoryEntity.ENTITY)
                .join(ProductToCategoryEntity.ENTITY)
                .on(DiscountToCategoryEntity.ENTITY.categoryId.eq(ProductToCategoryEntity.ENTITY.categoryId))
                .join(ProductVariantEntity.ENTITY)
                .on(ProductToCategoryEntity.ENTITY.productId.eq(ProductVariantEntity.ENTITY.productId))
                .where(ProductVariantEntity.ENTITY.id.eq(id))
        )
        return appliesProductVariantCondition.or(appliesProductCondition).or(appliesCategoryCondition)
    }

}