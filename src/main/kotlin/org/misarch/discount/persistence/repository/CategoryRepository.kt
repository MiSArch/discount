package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.CategoryEntity
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [CategoryEntity]s
 */
@Repository
interface CategoryRepository : QuerydslR2dbcRepository<CategoryEntity, UUID> {

    @Modifying
    @Query("INSERT INTO CategoryEntity (id) VALUES (:id)")
    suspend fun createCategory(
        @Param("id") id: UUID
    )

}