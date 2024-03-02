package org.misarch.discount.persistence.model

import org.misarch.discount.event.model.DiscountDTO
import org.misarch.discount.graphql.model.Discount
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.OffsetDateTime
import java.util.*

/**
 * Entity for addresses (both user and vendor)
 *
 * @property discount the discount applied to the order
 * @property maxUsagesPerUser the maximum number of times a user can use this discount in bought ProductItems
 * @property validUntil the date and time until which the discount is valid
 * @property validFrom the date and time from which the discount is valid
 * @property minOrderAmount the minimum order amount required to use this discount
 * @property id unique identifier of the address
 */
@Table
class DiscountEntity(
    val discount: Double,
    val maxUsagesPerUser: Int?,
    val validUntil: OffsetDateTime,
    val validFrom: OffsetDateTime,
    val minOrderAmount: Int?,
    @Id
    override val id: UUID?
) : BaseEntity<Discount> {

    companion object {
        /**
         * Querydsl entity
         */
        val ENTITY = QDiscountEntity.discountEntity!!
    }

    override fun toDTO(): Discount {
        return Discount(
            id = id!!,
            discount = discount,
            maxUsagesPerUser = maxUsagesPerUser,
            validUntil = validUntil,
            validFrom = validFrom,
            minOrderAmount = minOrderAmount
        )
    }

    /**
     * Converts the entity to an event DTO
     *
     * @return the event DTO
     */
    fun toEventDTO(): DiscountDTO {
        return DiscountDTO(
            id = id!!,
            discount = discount,
            maxUsagesPerUser = maxUsagesPerUser,
            validUntil = validUntil.toString(),
            validFrom = validFrom.toString(),
            minOrderAmount = minOrderAmount
        )
    }

}