package org.misarch.discount.persistence.model

import org.misarch.discount.graphql.model.User
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table
class UserEntity(
    @Id
    override val id: UUID
) : BaseEntity<User> {
    override fun toDTO(): User {
        return User(
            id = id
        )
    }

}