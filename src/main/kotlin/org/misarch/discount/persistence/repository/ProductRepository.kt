package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.ProductEntity
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [ProductEntity]s
 */
@Repository
interface ProductRepository : QuerydslR2dbcRepository<ProductEntity, UUID> {

    /**
     * Creates a product
     *
     * @param id the id of the product
     */
    @Modifying
    @Query("INSERT INTO ProductEntity (id) VALUES (:id)")
    suspend fun createProduct(
        @Param("id") id: UUID
    )

}