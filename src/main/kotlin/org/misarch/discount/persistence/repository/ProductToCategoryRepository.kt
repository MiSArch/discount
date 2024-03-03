package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.ProductToCategoryEntity
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [ProductToCategoryEntity]s
 */
@Repository
interface ProductToCategoryRepository : QuerydslR2dbcRepository<ProductToCategoryEntity, UUID>