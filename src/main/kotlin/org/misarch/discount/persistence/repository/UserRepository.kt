package org.misarch.discount.persistence.repository

import com.infobip.spring.data.r2dbc.QuerydslR2dbcRepository
import org.misarch.discount.persistence.model.UserEntity
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Repository for [UserEntity]s
 */
@Repository
interface UserRepository : QuerydslR2dbcRepository<UserEntity, UUID> {

    /**
     * Creates a user
     *
     * @param id the id of the user
     */
    @Modifying
    @Query("INSERT INTO UserEntity (id) VALUES (:id)")
    suspend fun createUser(
        @Param("id") id: UUID
    )

}