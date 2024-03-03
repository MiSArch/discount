package org.misarch.discount.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.authorizedUser
import java.time.OffsetDateTime
import java.util.*

@GraphQLDescription("A coupon")
@KeyDirective(fields = FieldSet("id"))
class Coupon(
    id: UUID,
    private val usages: Int,
    private val maxUsages: Int,
    @property:GraphQLDescription("The date and time until which the coupon is valid.")
    val validUntil: OffsetDateTime,
    @property:GraphQLDescription("The date and time from which the coupon is valid.")
    val validFrom: OffsetDateTime,
    @property:GraphQLDescription("The code of the coupon.")
    val code: String,
    private val discountId: UUID
) : Node(id) {

    @GraphQLDescription("The number of times the coupon has been used.")
    fun usages(dfe: DataFetchingEnvironment): Int {
        dfe.authorizedUser.checkIsEmployee()
        return usages
    }

    @GraphQLDescription("The maximum number of times the coupon can be used.")
    fun maxUsages(dfe: DataFetchingEnvironment): Int {
        dfe.authorizedUser.checkIsEmployee()
        return maxUsages
    }

}