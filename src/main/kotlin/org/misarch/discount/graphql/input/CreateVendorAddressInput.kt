package org.misarch.discount.graphql.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Input for the createVendorAddress mutation.")
class CreateVendorAddressInput(
    street1: String,
    street2: String,
    city: String,
    postalCode: String,
    country: String,
    companyName: String?,
) : CreateAddressInput(street1, street2, city, postalCode, country, companyName)