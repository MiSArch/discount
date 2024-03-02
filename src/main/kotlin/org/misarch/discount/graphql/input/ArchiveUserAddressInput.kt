package org.misarch.discount.graphql.input

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import java.util.*

@GraphQLDescription("Input for the archiveUserAddress mutation.")
class ArchiveUserAddressInput(
    @property:GraphQLDescription("The id of the user address to archive.")
    val id: UUID
)