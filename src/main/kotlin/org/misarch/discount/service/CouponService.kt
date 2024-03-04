package org.misarch.discount.service

import kotlinx.coroutines.reactor.awaitSingle
import org.misarch.discount.event.DiscountEvents
import org.misarch.discount.event.EventPublisher
import org.misarch.discount.graphql.input.CreateCouponInput
import org.misarch.discount.graphql.input.RegisterCouponInput
import org.misarch.discount.persistence.model.CouponEntity
import org.misarch.discount.persistence.model.CouponToUserEntity
import org.misarch.discount.persistence.repository.CouponRepository
import org.misarch.discount.persistence.repository.CouponToUserRepository
import org.misarch.discount.persistence.repository.DiscountRepository

/**
 * Service for [CouponEntity]s
 *
 * @param repository the provided repository
 * @param discountRepository the discount repository
 * @param couponToUserRepository the coupon to user repository
 * @param eventPublisher the event publisher
 */
class CouponService(
    repository: CouponRepository,
    private val discountRepository: DiscountRepository,
    private val couponToUserRepository: CouponToUserRepository,
    private val eventPublisher: EventPublisher
) : BaseService<CouponEntity, CouponRepository>(repository) {

    /**
     * Creates a coupon
     *
     * @param couponInput the coupon to create
     * @return the created coupon
     */
    suspend fun createCoupon(couponInput: CreateCouponInput): CouponEntity {
        require(
            discountRepository.existsById(couponInput.discountId).awaitSingle()
        ) { "Discount with id ${couponInput.discountId} does not exist" }
        val coupon = CouponEntity(
            maxUsages = couponInput.maxUsages,
            usages = 0,
            validUntil = couponInput.validUntil,
            validFrom = couponInput.validFrom,
            code = couponInput.code,
            discountId = couponInput.discountId,
            id = null
        )
        val savedCoupon = repository.save(coupon).awaitSingle()
        eventPublisher.publishEvent(DiscountEvents.COUPON_CREATED, savedCoupon.toEventDTO())
        return savedCoupon
    }

    /**
     * Registers a coupon usage by a user
     *
     * @param input the input for the registration, defines the coupon and the user
     * @return the registered coupon
     */
    suspend fun registerCoupon(input: RegisterCouponInput): CouponEntity {
        val coupon = repository.findByCode(input.code)
        require(coupon != null) { "Coupon with code ${input.code} does not exist" }
        couponToUserRepository.save(CouponToUserEntity(couponId = coupon.id!!, userId = input.userId, id = null))
            .awaitSingle()
        return repository.findById(coupon.id).awaitSingle()
    }

}