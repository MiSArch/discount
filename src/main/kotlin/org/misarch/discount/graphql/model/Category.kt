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
import org.misarch.discount.persistence.model.DiscountEntity
import org.misarch.discount.persistence.model.DiscountToCategoryEntity
import org.misarch.discount.persistence.repository.DiscountRepository
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

@GraphQLDescription("A category")
@KeyDirective(fields = FieldSet("id"))
class Category(
    id: UUID
) : Node(id) {

    @GraphQLDescription("Get all discounts which apply directly to this category")
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
            DiscountToCategoryEntity.ENTITY.categoryId.eq(id),
            orderBy,
            discountRepository,
            dfe.authorizedUserOrNull
        ) {
            it.innerJoin(DiscountToCategoryEntity.ENTITY)
                .on(DiscountToCategoryEntity.ENTITY.discountId.eq(DiscountEntity.ENTITY.id))
        }
    }

}