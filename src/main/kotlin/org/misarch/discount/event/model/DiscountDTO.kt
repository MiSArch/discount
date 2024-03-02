package org.misarch.discount.event.model

import java.util.*

/**
 * Discount DTO used for events
 *
 * @property id id of the address
 * @property discount the discount applied to the order
 * @property maxUsagesPerUser the maximum number of times a user can use this discount in bought ProductItems
 * @property validUntil the date and time until which the discount is valid
 * @property validFrom the date and time from which the discount is valid
 * @property minOrderAmount the minimum order amount required to use this discount
 */
class DiscountDTO(
    val id: UUID,
    val discount: Double,
    val maxUsagesPerUser: Int?,
    val validUntil: String,
    val validFrom: String,
    val minOrderAmount: Int?,
)