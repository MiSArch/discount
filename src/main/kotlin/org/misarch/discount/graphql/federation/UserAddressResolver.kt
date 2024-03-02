package org.misarch.discount.graphql.federation

import com.expediagroup.graphql.generator.federation.execution.FederatedTypePromiseResolver
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.dataloader.AddressDataLoader
import org.misarch.discount.graphql.model.Discount
import org.misarch.discount.graphql.model.UserAddress
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Federated resolver for [UserAddress]s.
 */
@Component
class UserAddressResolver : FederatedTypePromiseResolver<UserAddress> {
    override val typeName: String
        get() = UserAddress::class.simpleName!!

    override fun resolve(
        environment: DataFetchingEnvironment, representation: Map<String, Any>
    ): CompletableFuture<UserAddress?> {
        val id = representation["id"] as String?
        return if (id == null) {
            CompletableFuture.completedFuture(null)
        } else {
            environment.getDataLoader<UUID, Discount>(AddressDataLoader::class.simpleName!!)
                .load(UUID.fromString(id)).thenApply { it as? UserAddress }
        }
    }
}