package org.misarch.discount.graphql.federation

import com.expediagroup.graphql.generator.federation.execution.FederatedTypePromiseResolver
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.model.Product
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Federated resolver for [Product]s.
 */
@Component
class ProductResolver : FederatedTypePromiseResolver<Product> {
    override val typeName: String
        get() = Product::class.simpleName!!

    override fun resolve(
        environment: DataFetchingEnvironment, representation: Map<String, Any>
    ): CompletableFuture<Product?> {
        val id = representation["id"] as String?
        val uuid = UUID.fromString(id)
        return CompletableFuture.completedFuture(Product(uuid))
    }
}