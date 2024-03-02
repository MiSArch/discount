package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.misarch.discount.persistence.model.DiscountEntity
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [DiscountEntity]s
 */
@Repository
interface AddressRepository : QuerydslR2dbcRepository<DiscountEntity, UUID>


/**
 * Find the current vendor address
 *
 * @return the current vendor address or null if there is no current vendor address
 */
suspend fun AddressRepository.findCurrentVendorAddress(): DiscountEntity? {
    return query {
        val entity = DiscountEntity.ENTITY
        it.select(entityProjection())
            .from(entity)
            .where(entity.userId.isNull)
            .orderBy(entity.version.desc())
            .limit(1)
    }.first().awaitSingleOrNull()
}