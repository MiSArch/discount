package org.misarch.discount.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import java.util.*

@GraphQLDescription("An object with an ID.")
abstract class Node(
    @property:GraphQLDescription("The ID of the node.")
    val id: UUID
)