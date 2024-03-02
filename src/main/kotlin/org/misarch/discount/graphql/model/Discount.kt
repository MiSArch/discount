package org.misarch.discount.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import java.time.OffsetDateTime
import java.util.*

@GraphQLDescription("A discount.")
class Discount(
    id: UUID,
    @property:GraphQLDescription("The discount applied to the order, e.g. 0.2 meaning a 20% reduction in price.")
    val discount: Double,
    @property:GraphQLDescription("The maximum number of times a user can use this discount in bought ProductItems.")
    val maxUsagesPerUser: Int?,
    @property:GraphQLDescription("The date and time until which the discount is valid.")
    val validUntil: OffsetDateTime,
    @property:GraphQLDescription("The date and time from which the discount is valid.")
    val validFrom: OffsetDateTime,
    @property:GraphQLDescription("The minimum order amount required to use this discount.")
    val minOrderAmount: Int?
) : Node(id)