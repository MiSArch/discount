package org.misarch.discount.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.authorizedUser
import org.misarch.discount.graphql.authorizedUserOrNull
import org.misarch.discount.graphql.model.connection.DiscountConnection
import org.misarch.discount.graphql.model.connection.DiscountOrder
import org.misarch.discount.persistence.model.*
import org.misarch.discount.persistence.repository.DiscountRepository
import org.misarch.discount.service.DiscountService
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@GraphQLDescription("A product variant")
@KeyDirective(fields = FieldSet("id"))
class ProductVariant(
    id: UUID
) : Node(id) {

    @GraphQLDescription("Get all discounts which apply directly to this product variant")
    fun discounts(
        @GraphQLDescription("Number of items to return")
        first: Int? = null,
        @GraphQLDescription("Number of items to skip")
        skip: Int? = null,
        @GraphQLDescription("Ordering")
        orderBy: DiscountOrder? = null,
        @GraphQLIgnore
        @Autowired
        discountRepository: DiscountRepository, dfe: DataFetchingEnvironment
    ): DiscountConnection {
        dfe.authorizedUser.checkIsEmployee()
        return DiscountConnection(
            first,
            skip,
            DiscountToProductVariantEntity.ENTITY.productVariantId.eq(id),
            orderBy,
            discountRepository,
            dfe.authorizedUserOrNull
        ) {
            it.innerJoin(DiscountToProductVariantEntity.ENTITY)
                .on(DiscountToProductVariantEntity.ENTITY.discountId.eq(DiscountEntity.ENTITY.id))
        }
    }

    @GraphQLDescription("Get all discounts which apply to this product variant for the authenticated user")
    fun applicableDiscounts(
        @GraphQLDescription("Number of items to return")
        first: Int? = null,
        @GraphQLDescription("Number of items to skip")
        skip: Int? = null,
        @GraphQLDescription("Ordering")
        orderBy: DiscountOrder? = null,
        @GraphQLIgnore
        @Autowired
        discountRepository: DiscountRepository,
        @GraphQLIgnore
        @Autowired
        discountService: DiscountService,
        dfe: DataFetchingEnvironment
    ): DiscountConnection {
        val discountAppliesCondition = discountService.generateFullDiscountAppliesCondition(id, dfe.authorizedUserOrNull)
        return DiscountConnection(
            first, skip, discountAppliesCondition, orderBy, discountRepository, dfe.authorizedUserOrNull
        )
    }

}