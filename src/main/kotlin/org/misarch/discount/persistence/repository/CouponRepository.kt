package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.CouponEntity
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [CouponEntity]s
 */
@Repository
interface CouponRepository : QuerydslR2dbcRepository<CouponEntity, UUID> {

    /**
     * Finds a coupon by its code
     *
     * @param code the code of the coupon
     */
    suspend fun findByCode(code: String): CouponEntity?

}