package org.misarch.discount.graphql.federation

import com.expediagroup.graphql.generator.federation.execution.FederatedTypePromiseResolver
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.dataloader.UserDataLoader
import org.misarch.discount.graphql.model.User
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Federated resolver for [User]s.
 */
@Component
class UserResolver : FederatedTypePromiseResolver<User> {
    override val typeName: String
        get() = User::class.simpleName!!

    override fun resolve(
        environment: DataFetchingEnvironment, representation: Map<String, Any>
    ): CompletableFuture<User?> {
        val id = representation["id"] as String?
        val uuid = UUID.fromString(id)
        return CompletableFuture.completedFuture(User(uuid))
    }
}