package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.DiscountUsageEntity
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [DiscountUsageEntity]s
 */
@Repository
interface DiscountUsageRepository : QuerydslR2dbcRepository<DiscountUsageEntity, UUID> {

    /**
     * Upsert a discount usage
     *
     * @param discountId id of the discount
     * @param userId id of the user
     * @param usages the number of times the user has used the discount
     */
    @Modifying
    @Query(
        """
            INSERT INTO DiscountUsageEntity (discount_id, user_id, usages)
            VALUES (:discountId, :userId, :usages)
            ON CONFLICT (discount_id, user_id)
            DO UPDATE SET usages = DiscountUsageEntity.usages + :usages
        """
    )
    suspend fun upsertDiscountUsage(discountId: UUID, userId: UUID, usages: Long)

    /**
     * Find a discount usage by user id and discount id
     *
     * @param userId id of the user
     * @param discountId id of the discount
     * @return the discount usage, null if not found
     */
    suspend fun findByUserIdAndDiscountId(userId: UUID, discountId: UUID): DiscountUsageEntity?

}