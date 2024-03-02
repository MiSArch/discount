package org.misarch.discount.service

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import java.util.*

/**
 * Base service for all services
 *
 * @param T entity type
 * @param R repository type
 * @property repository the provided repository
 */
abstract class BaseService<T, R : QuerydslR2dbcRepository<T, UUID>>(protected val repository: R)