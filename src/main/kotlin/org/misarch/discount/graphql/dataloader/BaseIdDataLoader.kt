package org.misarch.discount.graphql.dataloader

import com.expediagroup.graphql.dataloader.KotlinDataLoader
import com.expediagroup.graphql.generator.extensions.get
import graphql.GraphQLContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import kotlinx.coroutines.reactor.awaitSingle
import org.dataloader.DataLoader
import org.dataloader.DataLoaderFactory
import org.dataloader.DataLoaderOptions
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.*
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Base class for data loaders which load entities by their id (with batch loading)
 *
 * @param T type of the DTO
 * @param D type of the entity
 * @property repository repository to load the entities from
 */
abstract class BaseIdDataLoader<T, D>(
    private val repository: ReactiveCrudRepository<D, UUID>
) :
    KotlinDataLoader<UUID, T> {
    override val dataLoaderName: String
        get() = this::class.simpleName!!

    override fun getDataLoader(graphQLContext: GraphQLContext): DataLoader<UUID, T> {
        return DataLoaderFactory.newDataLoader({ ids, batchLoaderEnvironment ->
            val contextScope = batchLoaderEnvironment.getContext<GraphQLContext>()?.get<CoroutineScope>()
            val coroutineScope = contextScope ?: CoroutineScope(EmptyCoroutineContext)
            coroutineScope.future {
                val entities = repository.findAllById(ids).collectList().awaitSingle().associateBy { key(it) }
                ids.map { toDTO(entities[it]!!) }
            }
        }, DataLoaderOptions.newOptions().setBatchLoaderContextProvider { graphQLContext })
    }

    /**
     * Converts the entity to a DTO.
     */
    abstract fun toDTO(entity: D): T

    /**
     * Returns the key of the entity.
     *
     * @param entity the entity
     * @return the key of the entity
     */
    abstract fun key(entity: D): UUID
}