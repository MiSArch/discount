package org.misarch.discount.graphql.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import graphql.schema.DataFetchingEnvironment
import org.misarch.discount.graphql.authorizedUser
import org.misarch.discount.graphql.authorizedUserOrNull
import org.misarch.discount.graphql.model.connection.*
import org.misarch.discount.graphql.model.connection.base.CommonOrder
import org.misarch.discount.persistence.model.*
import org.misarch.discount.persistence.repository.*
import org.springframework.beans.factory.annotation.Autowired
import java.time.OffsetDateTime
import java.util.*

@GraphQLDescription(
    """
    A discount.
    Multiple discounts are applied multiplicatively:
    discounted = original * (1 - discount1) * (1 - discount2) * ...
    """
)
@KeyDirective(fields = FieldSet("id"))
class Discount(
    id: UUID,
    @property:GraphQLDescription(
        """
        The discount applied to the order item, e.g. 0.2 meaning a 20% reduction in price.
        Multiple discounts are applied multiplicatively:
        discounted = original * (1 - discount1) * (1 - discount2) * ...
        """
    )
    val discount: Double,
    @property:GraphQLDescription("The maximum number of times a user can use this discount in bought ProductItems.")
    val maxUsagesPerUser: Int?,
    @property:GraphQLDescription("The date and time until which the discount is valid.")
    val validUntil: OffsetDateTime,
    @property:GraphQLDescription("The date and time from which the discount is valid.")
    val validFrom: OffsetDateTime,
    @property:GraphQLDescription("The minimum order amount required to use this discount.")
    val minOrderAmount: Int?
) : Node(id) {

    @GraphQLDescription("Get all categories to which this discount directly applies")
    fun discountAppliesToCategories(
        @GraphQLDescription("Number of items to return")
        first: Int? = null,
        @GraphQLDescription("Number of items to skip")
        skip: Int? = null,
        @GraphQLDescription("Ordering")
        orderBy: CommonOrder? = null,
        @GraphQLIgnore
        @Autowired
        categoryRepository: CategoryRepository, dfe: DataFetchingEnvironment
    ): CategoryConnection {
        dfe.authorizedUser.checkIsEmployee()
        return CategoryConnection(
            first,
            skip,
            DiscountToCategoryEntity.ENTITY.discountId.eq(id),
            orderBy,
            categoryRepository,
            dfe.authorizedUserOrNull
        ) {
            it.innerJoin(DiscountToCategoryEntity.ENTITY)
                .on(DiscountToCategoryEntity.ENTITY.categoryId.eq(CategoryEntity.ENTITY.id))
        }
    }

    @GraphQLDescription("Get all products to which this discount directly applies")
    fun discountAppliesToProducts(
        @GraphQLDescription("Number of items to return")
        first: Int? = null,
        @GraphQLDescription("Number of items to skip")
        skip: Int? = null,
        @GraphQLDescription("Ordering")
        orderBy: CommonOrder? = null,
        @GraphQLIgnore
        @Autowired
        productRepository: ProductRepository, dfe: DataFetchingEnvironment
    ): ProductConnection {
        dfe.authorizedUser.checkIsEmployee()
        return ProductConnection(
            first,
            skip,
            DiscountToProductEntity.ENTITY.discountId.eq(id),
            orderBy,
            productRepository,
            dfe.authorizedUserOrNull
        ) {
            it.innerJoin(DiscountToProductEntity.ENTITY)
                .on(DiscountToProductEntity.ENTITY.productId.eq(ProductEntity.ENTITY.id))
        }
    }

    @GraphQLDescription("Get all product variants to which this discount directly applies")
    fun discountAppliesToProductVariants(
        @GraphQLDescription("Number of items to return")
        first: Int? = null,
        @GraphQLDescription("Number of items to skip")
        skip: Int? = null,
        @GraphQLDescription("Ordering")
        orderBy: CommonOrder? = null,
        @GraphQLIgnore
        @Autowired
        productVariantRepository: ProductVariantRepository, dfe: DataFetchingEnvironment
    ): ProductVariantConnection {
        dfe.authorizedUser.checkIsEmployee()
        return ProductVariantConnection(
            first,
            skip,
            DiscountToProductVariantEntity.ENTITY.discountId.eq(id),
            orderBy,
            productVariantRepository,
            dfe.authorizedUserOrNull
        ) {
            it.innerJoin(DiscountToProductVariantEntity.ENTITY)
                .on(DiscountToProductVariantEntity.ENTITY.productVariantId.eq(ProductVariantEntity.ENTITY.id))
        }
    }

    @GraphQLDescription("Get all product variants to which this discount directly applies")
    fun discountRequiresCoupon(
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
        return CouponConnection(
            first,
            skip,
            filter,
            CouponEntity.ENTITY.discountId.eq(id),
            orderBy,
            couponRepository,
            dfe.authorizedUserOrNull
        )
    }

    @GraphQLDescription("Get all the usages of this discount by all users.")
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
            DiscountUsageEntity.ENTITY.discountId.eq(id),
            orderBy,
            discountUsageRepository,
            dfe.authorizedUserOrNull
        )
    }

}