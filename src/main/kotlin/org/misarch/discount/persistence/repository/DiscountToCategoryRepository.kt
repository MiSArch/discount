package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.DiscountToCategoryEntity
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [DiscountToCategoryEntity]s
 */
@Repository
interface DiscountToCategoryRepository : QuerydslR2dbcRepository<DiscountToCategoryEntity, UUID>