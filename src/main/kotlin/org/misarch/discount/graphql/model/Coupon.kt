package org.misarch.discount.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.authorizedUser
import org.misarch.discount.graphql.authorizedUserOrNull
import org.misarch.discount.graphql.dataloader.DiscountDataLoader
import org.misarch.discount.graphql.model.connection.UserConnection
import org.misarch.discount.graphql.model.connection.base.CommonOrder
import org.misarch.discount.persistence.model.CouponToUserEntity
import org.misarch.discount.persistence.model.DiscountToProductVariantEntity
import org.misarch.discount.persistence.model.ProductVariantEntity
import org.misarch.discount.persistence.model.UserEntity
import org.misarch.discount.persistence.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.CompletableFuture

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

    @GraphQLDescription("The discount granted by this coupon.")
    fun discount(
        dfe: DataFetchingEnvironment
    ): CompletableFuture<Discount> {
        return dfe.getDataLoader<UUID, Discount>(DiscountDataLoader::class.simpleName!!)
            .load(discountId, dfe)
    }

    @GraphQLDescription("Get all users who claimed this coupon.")
    fun users(
        @GraphQLDescription("Number of items to return")
        first: Int? = null,
        @GraphQLDescription("Number of items to skip")
        skip: Int? = null,
        @GraphQLDescription("Ordering")
        orderBy: CommonOrder? = null,
        @GraphQLIgnore
        @Autowired
        userRepository: UserRepository, dfe: DataFetchingEnvironment
    ): UserConnection {
        dfe.authorizedUser.checkIsEmployee()
        return UserConnection(
            first,
            skip,
            CouponToUserEntity.ENTITY.couponId.eq(id),
            orderBy,
            userRepository,
            dfe.authorizedUserOrNull
        ) {
            it.innerJoin(CouponToUserEntity.ENTITY)
                .on(CouponToUserEntity.ENTITY.userId.eq(UserEntity.ENTITY.id))
        }
    }

}