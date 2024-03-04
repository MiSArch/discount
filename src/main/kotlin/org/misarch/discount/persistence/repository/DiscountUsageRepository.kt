package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.DiscountUsageEntity
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [DiscountUsageEntity]s
 */
@Repository
interface DiscountUsageRepository : QuerydslR2dbcRepository<DiscountUsageEntity, UUID>