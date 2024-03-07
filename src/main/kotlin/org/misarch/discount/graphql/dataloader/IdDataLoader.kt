package org.misarch.discount.graphql.dataloader

import org.misarch.discount.persistence.model.BaseEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*

/**
 * Base class for data loaders which load entities by their id (with batch loading)
 *
 * @param T type of the DTO
 * @param D type of the entity
 * @property repository repository to load the entities from
 */
abstract class IdDataLoader<T, D : BaseEntity<T>>(repository: ReactiveCrudRepository<D, UUID>) :
    BaseIdDataLoader<T, D>(repository) {

    override fun toDTO(entity: D): T {
        return entity.toDTO()
    }

    override fun key(entity: D): UUID {
        return entity.id!!
    }
}