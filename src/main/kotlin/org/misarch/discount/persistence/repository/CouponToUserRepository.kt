package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.CouponToUserEntity
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [CouponToUserRepository]s
 */
@Repository
interface CouponToUserRepository : QuerydslR2dbcRepository<CouponToUserEntity, UUID>