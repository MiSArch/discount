package org.misarch.discount.persistence.model

import org.misarch.discount.event.model.CouponDTO
import org.misarch.discount.graphql.model.Coupon
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.ReadOnlyProperty
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Entity for coupons
 *
 * @property usages the number of times the coupon has been used
 * @property maxUsages the maximum number of times the coupon can be used
 * @property validUntil the date and time until which the coupon is valid
 * @property validFrom the date and time from which the coupon is valid
 * @property code the code of the coupon
 * @property discountId the id of the discount the coupon is for
 * @property id unique identifier of the coupon
 */
@Table
class CouponEntity(
    @ReadOnlyProperty
    val usages: Int,
    var maxUsages: Int?,
    var validUntil: OffsetDateTime,
    var validFrom: OffsetDateTime,
    var code: String,
    val discountId: UUID,
    @Id
    override val id: UUID?
) : BaseEntity<Coupon> {

    companion object {
        /**
         * Querydsl entity
         */
        val ENTITY = QCouponEntity.couponEntity!!
    }

    override fun toDTO(): Coupon {
        return Coupon(
            id = id!!,
            usages = usages,
            maxUsages = maxUsages,
            validUntil = validUntil,
            validFrom = validFrom,
            code = code,
            discountId = discountId
        )
    }

    /**
     * Converts the entity to an event DTO
     *
     * @return the event DTO
     */
    fun toEventDTO(): CouponDTO {
        return CouponDTO(
            id = id!!,
            maxUsages = maxUsages,
            validUntil = validUntil.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            validFrom = validFrom.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
            code = code,
            discountId = discountId
        )
    }

}