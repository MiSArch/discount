package org.misarch.discount.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.authorizedUser
import org.misarch.discount.graphql.authorizedUserOrNull
import org.misarch.discount.graphql.model.connection.*
import org.misarch.discount.persistence.model.CouponEntity
import org.misarch.discount.persistence.model.CouponToUserEntity
import org.misarch.discount.persistence.model.DiscountUsageEntity
import org.misarch.discount.persistence.model.UserEntity
import org.misarch.discount.persistence.repository.CouponRepository
import org.misarch.discount.persistence.repository.DiscountUsageRepository
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@GraphQLDescription("A user.")
@KeyDirective(fields = FieldSet("id"))
class User(
    id: UUID
) : Node(id) {

    @GraphQLDescription("Get all coupons this user has claimed")
    fun coupons(
        @GraphQLDescription("Number of items to return")
        first: Int? = null,
        @GraphQLDescription("Number of items to skip")
        skip: Int? = null,
        @GraphQLDescription("Ordering")
        orderBy: CouponOrder? = null,
        @GraphQLDescription("Filtering")
        filter: CouponFilter? = null,
        @GraphQLIgnore
        @Autowired
        couponRepository: CouponRepository, dfe: DataFetchingEnvironment
    ): CouponConnection {
        if (dfe.authorizedUser.id != id) {
            dfe.authorizedUser.checkIsEmployee()
        }
        return CouponConnection(
            first,
            skip,
            filter,
            CouponToUserEntity.ENTITY.userId.eq(id),
            orderBy,
            couponRepository,
            dfe.authorizedUserOrNull
        ) {
            it.innerJoin(CouponToUserEntity.ENTITY)
                .on(CouponToUserEntity.ENTITY.couponId.eq(CouponEntity.ENTITY.id))
        }
    }

    @GraphQLDescription("Get all the discount usages by this user")
    fun discountUsages(
        @GraphQLDescription("Number of items to return")
        first: Int? = null,
        @GraphQLDescription("Number of items to skip")
        skip: Int? = null,
        @GraphQLDescription("Ordering")
        orderBy: DiscountUsageOrder? = null,
        @GraphQLIgnore
        @Autowired
        discountUsageRepository: DiscountUsageRepository, dfe: DataFetchingEnvironment
    ): DiscountUsageConnection {
        dfe.authorizedUser.checkIsEmployee()
        return DiscountUsageConnection(
            first,
            skip,
            DiscountUsageEntity.ENTITY.userId.eq(id),
            orderBy,
            discountUsageRepository,
            dfe.authorizedUserOrNull
        )
    }

}