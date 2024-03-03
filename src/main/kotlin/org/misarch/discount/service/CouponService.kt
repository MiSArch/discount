package org.misarch.discount.service

import kotlinx.coroutines.reactor.awaitSingle
import org.misarch.discount.event.DiscountEvents
import org.misarch.discount.event.EventPublisher
import org.misarch.discount.graphql.input.CreateCouponInput
import org.misarch.discount.persistence.model.CouponEntity
import org.misarch.discount.persistence.repository.CouponRepository
import org.misarch.discount.persistence.repository.DiscountRepository

/**
 * Service for [CouponEntity]s
 *
 * @param repository the provided repository
 * @param discountRepository the discount repository
 * @param eventPublisher the event publisher
 */
class CouponService(
    repository: CouponRepository,
    private val discountRepository: DiscountRepository,
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

}