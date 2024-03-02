package org.misarch.discount.graphql

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.dataloader.AddressDataLoader
import org.misarch.discount.graphql.model.Discount
import org.misarch.discount.graphql.model.UserAddress
import org.misarch.discount.graphql.model.VendorAddress
import org.misarch.discount.persistence.repository.AddressRepository
import org.misarch.discount.persistence.repository.findCurrentVendorAddress
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * Defines GraphQL queries
 *
 * @property addressRepository repository for addresses
 */
@Component
class Query(
    private val addressRepository: AddressRepository,
) : Query {

    @GraphQLDescription("Get a address by id")
    fun address(
        @GraphQLDescription("The id of the address")
        id: UUID,
        dfe: DataFetchingEnvironment
    ): CompletableFuture<Discount> {
        return dfe.getDataLoader<UUID, Discount>(AddressDataLoader::class.simpleName!!).load(id).thenApply {
            if (it is UserAddress) {
                val authorizedUser = dfe.authorizedUser
                if (it.userId != authorizedUser.id) {
                    authorizedUser.checkIsEmployee()
                }
            }
            it
        }
    }

    @GraphQLDescription("Get the current vendor address")
    suspend fun vendorAddress(): VendorAddress? {
        return addressRepository.findCurrentVendorAddress()?.toDTO() as VendorAddress?
    }

}