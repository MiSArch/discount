package org.misarch.discount.graphql.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

/**
 * Base class for [CreateUserAddressInput] and [CreateVendorAddressInput]
 */
abstract class CreateAddressInput(
    @property:GraphQLDescription("The first part of the street part of the address to create")
    val street1: String,
    @property:GraphQLDescription("The second part of the street part of the address to create")
    val street2: String,
    @property:GraphQLDescription("The city part of the address to create")
    val city: String,
    @property:GraphQLDescription("The postal code part of the address to create")
    val postalCode: String,
    @property:GraphQLDescription("The country part of the address to create")
    val country: String,
    @property:GraphQLDescription("The company name part of the address to create")
    val companyName: String?
)