package org.misarch.discount.graphql.federation

import com.expediagroup.graphql.generator.federation.execution.FederatedTypePromiseResolver
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.dataloader.AddressDataLoader
import org.misarch.discount.graphql.model.Discount
import org.misarch.discount.graphql.model.VendorAddress
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Federated resolver for [VendorAddress]s.
 */
@Component
class VendorAddressResolver : FederatedTypePromiseResolver<VendorAddress> {
    override val typeName: String
        get() = VendorAddress::class.simpleName!!

    override fun resolve(
        environment: DataFetchingEnvironment, representation: Map<String, Any>
    ): CompletableFuture<VendorAddress?> {
        val id = representation["id"] as String?
        return if (id == null) {
            CompletableFuture.completedFuture(null)
        } else {
            environment.getDataLoader<UUID, Discount>(AddressDataLoader::class.simpleName!!)
                .load(UUID.fromString(id)).thenApply { it as? VendorAddress }
        }
    }
}