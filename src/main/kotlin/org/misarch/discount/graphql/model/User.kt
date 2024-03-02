package org.misarch.discount.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.authorizedUser
import org.misarch.discount.graphql.authorizedUserOrNull
import org.misarch.discount.graphql.model.connection.UserAddressConnection
import org.misarch.discount.graphql.model.connection.UserAddressFilter
import org.misarch.discount.graphql.model.connection.UserAddressOrder
import org.misarch.discount.persistence.model.DiscountEntity
import org.misarch.discount.persistence.repository.AddressRepository
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@GraphQLDescription("A user.")
@KeyDirective(fields = FieldSet("id"))
class User(
    id: UUID
) : Node(id) {

    @GraphQLDescription("Get all address the user received")
    fun addresses(
        @GraphQLDescription("Number of items to return")
        first: Int? = null,
        @GraphQLDescription("Number of items to skip")
        skip: Int? = null,
        @GraphQLDescription("Ordering")
        orderBy: UserAddressOrder? = null,
        @GraphQLDescription("Filtering")
        filter: UserAddressFilter? = null,
        @GraphQLIgnore
        @Autowired
        productVariantRepository: AddressRepository,
        dfe: DataFetchingEnvironment
    ): UserAddressConnection {
        if (dfe.authorizedUser.id != id) {
            dfe.authorizedUser.checkIsEmployee()
        }
        return UserAddressConnection(
            first, skip, filter, DiscountEntity.ENTITY.userId.eq(id), orderBy, productVariantRepository, dfe.authorizedUserOrNull
        )
    }

}