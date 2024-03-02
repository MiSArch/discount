package org.misarch.discount.graphql.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import java.util.*

@GraphQLDescription("Input for the createUserAddress mutation.")
class CreateUserAddressInput(
    street1: String,
    street2: String,
    city: String,
    postalCode: String,
    country: String,
    companyName: String?,
    @property:GraphQLDescription("The id of the user to create the address for.")
    val userId: UUID,
) : CreateAddressInput(street1, street2, city, postalCode, country, companyName)