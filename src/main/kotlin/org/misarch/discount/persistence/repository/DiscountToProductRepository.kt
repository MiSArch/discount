package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.DiscountToProductEntity
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [DiscountToProductEntity]s
 */
@Repository
interface DiscountToProductRepository : QuerydslR2dbcRepository<DiscountToProductEntity, UUID> {

    /**
     * Finds [DiscountToProductEntity]s by discount id
     *
     * @param discountId the discount id
     * @return the found [DiscountToProductEntity]s
     */
    suspend fun findByDiscountId(discountId: UUID): List<DiscountToProductEntity>

    /**
     * Deletes [DiscountToProductEntity]s by discount id and product id
     *
     * @param discountId the discount id
     * @param productId the product id
     */
    suspend fun deleteByDiscountIdAndProductId(discountId: UUID, productId: UUID)

}