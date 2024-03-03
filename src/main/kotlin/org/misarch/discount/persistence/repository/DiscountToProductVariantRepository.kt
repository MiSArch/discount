package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.DiscountToProductVariantEntity
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [DiscountToProductVariantEntity]s
 */
@Repository
interface DiscountToProductVariantRepository : QuerydslR2dbcRepository<DiscountToProductVariantEntity, UUID>