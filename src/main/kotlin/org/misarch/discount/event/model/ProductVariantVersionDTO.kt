package org.misarch.discount.event.model

import java.util.*

/**
 * ProductVariantVersion DTO used for events
 *
 * @property id id of the productVariantVersion
 * @property productVariantId id of the product variant
 */
data class ProductVariantVersionDTO(
    val id: UUID,
    val productVariantId: UUID
)