package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.CouponRedemptionEntity
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [CouponRedemptionRepository]s
 */
@Repository
interface CouponRedemptionRepository : QuerydslR2dbcRepository<CouponRedemptionEntity, UUID>