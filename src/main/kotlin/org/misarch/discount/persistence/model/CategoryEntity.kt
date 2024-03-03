package org.misarch.discount.persistence.model

import org.misarch.discount.graphql.model.Category
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

/**
 * Entity for categories
 *
 * @property id unique identifier of the category
 */
@Table
class CategoryEntity(
    @Id
    override val id: UUID?
) : BaseEntity<Category> {

    companion object {
        /**
         * Querydsl entity
         */
        val ENTITY = QCategoryEntity.categoryEntity!!
    }

    override fun toDTO(): Category {
        return Category(
            id = id!!,
        )
    }
}