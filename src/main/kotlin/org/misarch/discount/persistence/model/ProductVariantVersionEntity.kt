package org.misarch.discount.persistence.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

/**
 * Entity for product variant versions
 *
 * @property productVariantId unique identifier of the product variant version
 * @property id unique identifier of the product variant version
 */
@Table
class ProductVariantVersionEntity(
    val productVariantId: UUID,
    @Id
    val id: UUID?
) {

    companion object {
        /**
         * Querydsl entity
         */
        val ENTITY = QProductVariantVersionEntity.productVariantVersionEntity!!
    }
}