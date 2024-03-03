package org.misarch.discount.service

import org.misarch.discount.event.model.CategoryDTO
import org.misarch.discount.persistence.model.CategoryEntity
import org.misarch.discount.persistence.repository.CategoryRepository
import org.springframework.stereotype.Service

/**
 * Service for [CategoryEntity]s
 *
 * @param repository the repository for [CategoryEntity]s
 */
@Service
class CategoryService(
    repository: CategoryRepository
) : BaseService<CategoryEntity, CategoryRepository>(repository) {

    /**
     * Registers a category
     *
     * @param categoryDTO the category to register
     */
    suspend fun registerCategory(categoryDTO: CategoryDTO) {
        repository.createCategory(categoryDTO.id)
    }

}