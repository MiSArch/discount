package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.DiscountToProductVariantEntity
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [DiscountToProductVariantEntity]s
 */
@Repository
interface DiscountToProductVariantRepository : QuerydslR2dbcRepository<DiscountToProductVariantEntity, UUID> {

    /**
     * Finds [DiscountToProductVariantEntity]s by discount id
     *
     * @param discountId the discount id
     * @return the found [DiscountToProductVariantEntity]s
     */
    suspend fun findByDiscountId(discountId: UUID): List<DiscountToProductVariantEntity>

    /**
     * Deletes [DiscountToProductVariantEntity]s by discount id and product variant id
     *
     * @param discountId the discount id
     * @param productVariantId the product variant id
     */
    suspend fun deleteByDiscountIdAndProductVariantId(discountId: UUID, productVariantId: UUID)

}