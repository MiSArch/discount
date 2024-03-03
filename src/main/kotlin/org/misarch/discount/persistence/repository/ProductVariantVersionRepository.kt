package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.ProductVariantVersionEntity
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [ProductVariantVersionEntity]s
 */
@Repository
interface ProductVariantVersionRepository : QuerydslR2dbcRepository<ProductVariantVersionEntity, UUID> {

    /**
     * Creates a productVariantVersion
     *
     * @param id the id of the productVariantVersion
     * @param productVariantId the id of the product variant
     */
    @Modifying
    @Query("INSERT INTO ProductVariantVersionEntity (id, productVariantId) VALUES (:id, :productVariantId)")
    suspend fun createProductVariantVersion(
        @Param("id") id: UUID,
        @Param("productVariantId") productVariantId: UUID
    )

}