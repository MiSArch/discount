package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.DiscountToCategoryEntity
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [DiscountToCategoryEntity]s
 */
@Repository
interface DiscountToCategoryRepository : QuerydslR2dbcRepository<DiscountToCategoryEntity, UUID> {

    /**
     * Finds [DiscountToCategoryEntity]s by discount id
     *
     * @param discountId the discount id
     * @return the found [DiscountToCategoryEntity]s
     */
    suspend fun findByDiscountId(discountId: UUID): List<DiscountToCategoryEntity>

    /**
     * Deletes [DiscountToCategoryEntity]s by discount id and category id
     *
     * @param discountId the discount id
     * @param categoryId the category id
     */
    suspend fun deleteByDiscountIdAndCategoryId(discountId: UUID, categoryId: UUID)

}