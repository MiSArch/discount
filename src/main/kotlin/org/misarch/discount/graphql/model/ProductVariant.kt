package org.misarch.discount.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.authorizedUser
import org.misarch.discount.graphql.authorizedUserOrNull
import org.misarch.discount.graphql.model.connection.DiscountConnection
import org.misarch.discount.graphql.model.connection.DiscountOrder
import org.misarch.discount.persistence.model.DiscountEntity
import org.misarch.discount.persistence.model.DiscountToProductVariantEntity
import org.misarch.discount.persistence.repository.DiscountRepository
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@GraphQLDescription("A product variant")
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
    
}