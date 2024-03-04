package org.misarch.discount.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Mutation
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.input.CreateCouponInput
import org.misarch.discount.graphql.input.CreateDiscountInput
import org.misarch.discount.graphql.input.RegisterCouponInput
import org.misarch.discount.graphql.model.Coupon
import org.misarch.discount.graphql.model.Discount
import org.misarch.discount.service.CouponService
import org.misarch.discount.service.DiscountService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * Defines GraphQL mutations
 *
 * @property discountService service used to create and update discounts
 * @property couponService service used to create and update coupons
 */
@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
class Mutation(
    private val discountService: DiscountService,
    private val couponService: CouponService
) : Mutation {

    @GraphQLDescription("Create a new discount")
    suspend fun createDiscount(
        @GraphQLDescription("Input for the createDiscount mutation")
        input: CreateDiscountInput,
        dfe: DataFetchingEnvironment
    ): Discount {
        dfe.authorizedUser.checkIsEmployee()
        val discount = discountService.createDiscount(input)
        return discount.toDTO()
    }

    @GraphQLDescription("Create a new coupon")
    suspend fun createCoupon(
        @GraphQLDescription("Input for the createCoupon mutation")
        input: CreateCouponInput,
        dfe: DataFetchingEnvironment
    ): Coupon {
        dfe.authorizedUser.checkIsEmployee()
        val coupon = couponService.createCoupon(input)
        return coupon.toDTO()
    }

    suspend fun registerCoupon(
        @GraphQLDescription("Input for the registerCoupon mutation")
        input: RegisterCouponInput,
        dfe: DataFetchingEnvironment
    ): Coupon {
        if (dfe.authorizedUser.id != input.userId) {
            dfe.authorizedUser.checkIsEmployee()
        }
        val coupon = couponService.registerCoupon(input)
        return coupon.toDTO()
    }
}