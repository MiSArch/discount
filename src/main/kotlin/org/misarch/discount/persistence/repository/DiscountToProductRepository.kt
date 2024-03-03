package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.DiscountToProductEntity
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [DiscountToProductEntity]s
 */
@Repository
interface DiscountToProductRepository : QuerydslR2dbcRepository<DiscountToProductEntity, UUID>