package org.misarch.discount.service

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Coalesce
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.sql.SQLExpressions
import kotlinx.coroutines.reactor.awaitSingle
import org.misarch.discount.event.DiscountEvents
import org.misarch.discount.event.EventPublisher
import org.misarch.discount.event.model.ValidationFailedDTO
import org.misarch.discount.event.model.ValidationSucceededDTO
import org.misarch.discount.event.model.order.OrderDTO
import org.misarch.discount.graphql.AuthorizedUser
import org.misarch.discount.graphql.input.CreateDiscountInput
import org.misarch.discount.graphql.input.FindApplicableDiscountsInput
import org.misarch.discount.graphql.input.UpdateDiscountInput
import org.misarch.discount.persistence.model.*
import org.misarch.discount.persistence.repository.*
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.*

/**
 * Service for [DiscountEntity]s
 *
 * @param repository the provided repository
 * @param categoryRepository the category repository
 * @param productRepository the product repository
 * @param productVariantRepository the product variant repository
 * @param discountToCategoryRepository the discount to category repository
 * @param discountToProductRepository the discount to product repository
 * @param discountToProductVariantRepository the discount to product variant repository
 * @param discountUsageRepository the discount usage repository
 * @param couponRepository the coupon repository
 * @param eventPublisher the event publisher
 */
@Service
class DiscountService(
    repository: DiscountRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val productVariantRepository: ProductVariantRepository,
    private val discountToCategoryRepository: DiscountToCategoryRepository,
    private val discountToProductRepository: DiscountToProductRepository,
    private val discountToProductVariantRepository: DiscountToProductVariantRepository,
    private val discountUsageRepository: DiscountUsageRepository,
    private val couponRepository: CouponRepository,
    private val eventPublisher: EventPublisher
) : BaseService<DiscountEntity, DiscountRepository>(repository) {

    /**
     * Creates a discount
     *
     * @param discountInput the discount to create
     * @return the created discount
     */
    suspend fun createDiscount(discountInput: CreateDiscountInput): DiscountEntity {
        ensureReferencedEntitiesExist(discountInput)
        val discount = DiscountEntity(
            discount = discountInput.discount,
            maxUsagesPerUser = discountInput.maxUsagesPerUser,
            validUntil = discountInput.validUntil,
            validFrom = discountInput.validFrom,
            minOrderAmount = discountInput.minOrderAmount,
            id = null
        )
        val savedDiscount = repository.save(discount).awaitSingle()
        val categoryIds = discountInput.discountAppliesToCategoryIds.toSet()
        val productIds = discountInput.discountAppliesToProductIds.toSet()
        val productVariantIds = discountInput.discountAppliesToProductVariantIds.toSet()
        addAppliedToReferences(savedDiscount.id!!, categoryIds, productIds, productVariantIds)
        eventPublisher.publishEvent(
            DiscountEvents.DISCOUNT_CREATED, savedDiscount.toEventDTO(categoryIds, productIds, productVariantIds)
        )
        return savedDiscount
    }

    /**
     * Updates a discount
     *
     * @param discountInput the input for the update
     * @return the updated discount
     */
    suspend fun updateDiscount(discountInput: UpdateDiscountInput): DiscountEntity {
        val discount = repository.findById(discountInput.id).awaitSingle()
        if (discountInput.maxUsagesPerUser is OptionalInput.Defined) {
            discount.maxUsagesPerUser = discountInput.maxUsagesPerUser.value
        }
        if (discountInput.validUntil != null) {
            discount.validUntil = discountInput.validUntil
        }
        if (discountInput.validFrom != null) {
            discount.validFrom = discountInput.validFrom
        }
        if (discountInput.minOrderAmount is OptionalInput.Defined) {
            discount.minOrderAmount = discountInput.minOrderAmount.value
        }
        updateDiscountReferencedEntities(discountInput)
        val updatedDiscount = repository.save(discount).awaitSingle()
        eventPublisher.publishEvent(
            DiscountEvents.DISCOUNT_UPDATED, updatedDiscount.toEventDTO(
                discountToCategoryRepository.findByDiscountId(discountInput.id).map { it.categoryId }.toSet(),
                discountToProductRepository.findByDiscountId(discountInput.id).map { it.productId }.toSet(),
                discountToProductVariantRepository.findByDiscountId(discountInput.id).map { it.productVariantId }
                    .toSet()
            )
        )
        return updatedDiscount
    }

    /**
     * Updates the entities the discount applies to, and removes the entities the discount no longer applies to
     *
     * @param discountInput the input for the update
     * @throws IllegalArgumentException if any of the referenced entities do not exist
     */
    private suspend fun updateDiscountReferencedEntities(discountInput: UpdateDiscountInput) {
        val categoryIds = discountInput.addedDiscountAppliesToCategoryIds?.toSet() ?: emptySet()
        val productIds = discountInput.addedDiscountAppliesToProductIds?.toSet() ?: emptySet()
        val productVariantIds = discountInput.addedDiscountAppliesToProductVariantIds?.toSet() ?: emptySet()
        ensureEntitiesExist(categoryIds, productIds, productVariantIds)
        addAppliedToReferences(discountInput.id, categoryIds, productIds, productVariantIds)
        discountInput.removedDiscountAppliesToCategoryIds?.forEach {
            discountToCategoryRepository.deleteByDiscountIdAndCategoryId(discountInput.id, it)
        }
        discountInput.removedDiscountAppliesToProductIds?.forEach {
            discountToProductRepository.deleteByDiscountIdAndProductId(discountInput.id, it)
        }
        discountInput.removedDiscountAppliesToProductVariantIds?.forEach {
            discountToProductVariantRepository.deleteByDiscountIdAndProductVariantId(discountInput.id, it)
        }
    }

    /**
     * Checks that the entities referenced by [discountInput] exist
     *
     * @param discountInput the discount input
     * @throws IllegalArgumentException if any of the referenced entities do not exist
     */
    private suspend fun ensureReferencedEntitiesExist(discountInput: CreateDiscountInput) {
        val categoryIds = discountInput.discountAppliesToCategoryIds.toSet()
        val productIds = discountInput.discountAppliesToProductIds.toSet()
        val productVariantIds = discountInput.discountAppliesToProductVariantIds.toSet()
        require(categoryIds.isNotEmpty() || productIds.isNotEmpty() || productVariantIds.isNotEmpty()) {
            "At least one category, product or product variant must be specified"
        }
        ensureEntitiesExist(categoryIds, productIds, productVariantIds)
    }

    /**
     * Checks that the entities referenced by [categoryIds], [productIds] and [productVariantIds] exist
     *
     * @param categoryIds the category ids
     * @param productIds the product ids
     * @param productVariantIds the product variant ids
     * @throws IllegalArgumentException if any of the referenced entities do not exist
     */
    private suspend fun ensureEntitiesExist(
        categoryIds: Set<UUID>, productIds: Set<UUID>, productVariantIds: Set<UUID>
    ) {
        val missingCategories = categoryIds.filter { !categoryRepository.existsById(it).awaitSingle() }
        require(missingCategories.isEmpty()) { "Categories with ids $missingCategories do not exist" }
        val missingProducts = productIds.filter { !productRepository.existsById(it).awaitSingle() }
        require(missingProducts.isEmpty()) { "Products with ids $missingProducts do not exist" }
        val missingProductVariants = productVariantIds.filter {
            !productVariantRepository.existsById(it).awaitSingle()
        }
        require(missingProductVariants.isEmpty()) { "Product variants with ids $missingProductVariants do not exist" }
    }

    /**
     * Adds the applied to references to the discount
     *
     * @param id the id of the discount
     * @param discountAppliesToCategoryIds the category ids to which the discount applies
     * @param discountAppliesToProductIds the product ids to which the discount applies
     * @param discountAppliesToProductVariantIds the product variant ids to which the discount applies
     */
    private suspend fun addAppliedToReferences(
        id: UUID,
        discountAppliesToCategoryIds: Set<UUID>,
        discountAppliesToProductIds: Set<UUID>,
        discountAppliesToProductVariantIds: Set<UUID>
    ) {
        discountToCategoryRepository.saveAll(discountAppliesToCategoryIds.map {
            DiscountToCategoryEntity(discountId = id, categoryId = it, id = null)
        }).collectList().awaitSingle()
        discountToProductRepository.saveAll(discountAppliesToProductIds.map {
            DiscountToProductEntity(discountId = id, productId = it, id = null)
        }).collectList().awaitSingle()
        discountToProductVariantRepository.saveAll(discountAppliesToProductVariantIds.map {
            DiscountToProductVariantEntity(discountId = id, productVariantId = it, id = null)
        }).collectList().awaitSingle()
    }

    /**
     * Validates an order
     * Checks if the user can still use the discounts with the amount of items
     * Does NOT validate if the discount is even usable with the items in the order
     *
     * @param order the order to validate
     */
    suspend fun validateOrder(order: OrderDTO) {
        try {
            validateOrderInternal(order)
        } catch (e: Exception) {
            eventPublisher.publishEvent(DiscountEvents.VALIDATION_FAILED, ValidationFailedDTO(order, emptyList()))
            throw e
        }
    }

    /**
     * Validates an order
     * Checks if the user can still use the discounts with the amount of items
     * Does NOT validate if the discount is even usable with the items in the order
     * Does not handle exceptions!
     *
     * @param order the order to validate
     */
    private suspend fun validateOrderInternal(order: OrderDTO) {
        val orderItemsByDiscount = order.orderItems.flatMap { orderItem ->
            orderItem.discountIds.map { it to orderItem }
        }.groupBy({ it.first }) { it.second }
        val discounts =
            repository.findAllById(orderItemsByDiscount.keys).collectList().awaitSingle().associateBy { it.id }
        val failedDiscounts = mutableListOf<UUID>()
        orderItemsByDiscount.forEach { (discount, orderItems) ->
            val totalAmount = orderItems.sumOf { it.count }
            val currentAmount = discountUsageRepository.findByUserIdAndDiscountId(order.userId, discount)?.usages ?: 0
            if (currentAmount + totalAmount > (discounts[discount]!!.maxUsagesPerUser?.toLong() ?: Long.MAX_VALUE)) {
                failedDiscounts += discount
            }
        }
        if (failedDiscounts.isNotEmpty()) {
            eventPublisher.publishEvent(DiscountEvents.VALIDATION_FAILED, ValidationFailedDTO(order, failedDiscounts))
        } else {
            orderItemsByDiscount.forEach { (discount, orderItems) ->
                discountUsageRepository.upsertDiscountUsage(discount, order.userId, orderItems.sumOf { it.count })
            }
            eventPublisher.publishEvent(DiscountEvents.VALIDATION_SUCCEEDED, ValidationSucceededDTO(order))
        }
    }

    /**
     * Finds all discounts that are applicable to a product variant, a user and a list of coupons.
     * Also checks if the user can use the discounts with the amount of items.
     *
     * @param input the input for the query
     * @return The applicable discounts
     * @throws IllegalArgumentException if any of the coupons are not applicable, or multiple coupons are used for the same discount
     */
    suspend fun findApplicableDiscounts(
        input: FindApplicableDiscountsInput
    ): List<DiscountEntity> {
        val condition = generateApplicableCouponsFilterCondition(input)
        val discounts = repository.query {
            it.select(repository.entityProjection()).from(DiscountEntity.ENTITY).leftJoin(DiscountUsageEntity.ENTITY)
                .on(
                    DiscountEntity.ENTITY.id.eq(DiscountUsageEntity.ENTITY.discountId)
                        .and(DiscountUsageEntity.ENTITY.userId.eq(input.userId))
                ).where(condition)
        }.all().collectList().awaitSingle()
        val discountsById = discounts.associateBy { it.id }
        val coupons = couponRepository.findAllById(input.couponIds).collectList().awaitSingle()
        coupons.forEach {
            require(it.discountId in discountsById) {
                "Coupon with id ${it.id} could not be used, either due to insufficient remaining usages, the user not owning the coupon, or the coupon not being applicable"
            }
        }
        coupons.groupBy({ it.discountId }) { it.id }.filter { it.value.size > 1 }.forEach { (discountId, couponsIds) ->
            error("Coupons with ids $couponsIds are used multiple times for the same discount $discountId")
        }
        return discounts
    }

    /**
     * Generates a condition that checks that a discount is applicable to a product variant, a user and a list of coupons.
     *
     * @param input the input for the query
     * @return The condition
     */
    private fun generateApplicableCouponsFilterCondition(input: FindApplicableDiscountsInput): BooleanExpression? {
        val appliesCondition = generateDiscountAppliesCondition(input.productVariantId)
        val noCouponsCondition = generateNoCouponsCondition()
        val couponsCondition = DiscountEntity.ENTITY.id.`in`(
            SQLExpressions.select(CouponEntity.ENTITY.discountId).from(CouponEntity.ENTITY)
                .join(CouponToUserEntity.ENTITY).on(CouponToUserEntity.ENTITY.couponId.eq(CouponEntity.ENTITY.id))
                .where(
                    CouponEntity.ENTITY.id.`in`(input.couponIds).and(CouponToUserEntity.ENTITY.userId.eq(input.userId))
                )
        )
        val usagesCondition = DiscountEntity.ENTITY.maxUsagesPerUser.isNull.or(
            DiscountEntity.ENTITY.maxUsagesPerUser.goe(
                Coalesce(
                    DiscountUsageEntity.ENTITY.usages.add(input.count), Expressions.constant(input.count)
                )
            )
        )
        val currentlyValidCondition = generateIsCurrentlyValidCondition()
        val condition = appliesCondition.and(usagesCondition).and(noCouponsCondition.or(couponsCondition))
            .and(currentlyValidCondition)
        return condition
    }

    /**
     * Generates a condition that checks that a discount either applies to the product variant, the owning product,
     * or any of the categories of the product.
     * Also checks that either no coupons are required, or the user has a coupon for the discount.
     *
     * @param id the id of the product variant
     * @param authorizedUser the authorized user, or null if the user is not authenticated
     * @return The condition
     */
    fun generateFullDiscountAppliesCondition(id: UUID, authorizedUser: AuthorizedUser?): BooleanExpression {
        val appliesCondition = generateDiscountAppliesCondition(id)
        val hasNoRequiredCouponsCondition = generateNoCouponsCondition()
        val couponsCondition = if (authorizedUser == null) {
            hasNoRequiredCouponsCondition
        } else {
            val userHasCouponCondition =
                SQLExpressions.select(Expressions.TRUE).from(CouponEntity.ENTITY).join(CouponToUserEntity.ENTITY)
                    .on(CouponEntity.ENTITY.id.eq(CouponToUserEntity.ENTITY.couponId)).where(
                        CouponEntity.ENTITY.discountId.eq(DiscountEntity.ENTITY.id)
                            .and(CouponToUserEntity.ENTITY.userId.eq(authorizedUser.id))
                    ).exists()
            hasNoRequiredCouponsCondition.or(userHasCouponCondition)
        }
        val currentlyValidCondition = generateIsCurrentlyValidCondition()
        val discountAppliesCondition = appliesCondition.and(couponsCondition).and(currentlyValidCondition)
        return discountAppliesCondition
    }

    /**
     * Generates a condition that checks that a discount is currently valid
     *
     * @return The condition
     */
    private fun generateIsCurrentlyValidCondition(): BooleanExpression {
        val now = Expressions.constant(OffsetDateTime.now())
        return DiscountEntity.ENTITY.validFrom.loe(now).and(DiscountEntity.ENTITY.validUntil.goe(now))
    }

    /**
     * Generates a condition that checks that no coupons are required for a discount
     *
     * @return The condition
     */
    private fun generateNoCouponsCondition(): BooleanExpression {
        val hasNoRequiredCouponsCondition = SQLExpressions.select(Expressions.TRUE).from(CouponEntity.ENTITY)
            .where(CouponEntity.ENTITY.discountId.eq(DiscountEntity.ENTITY.id)).notExists()
        return hasNoRequiredCouponsCondition
    }

    /**
     * Generates a condition that checks that a discount either applies to the product variant, the owning product,
     * or any of the categories of the product.
     * Does NOT check for required coupons.
     *
     * @param id the id of the product variant
     * @return The condition
     */
    fun generateDiscountAppliesCondition(id: UUID): BooleanExpression {
        val appliesProductVariantCondition = DiscountEntity.ENTITY.id.`in`(
            SQLExpressions.select(DiscountToProductVariantEntity.ENTITY.discountId)
                .from(DiscountToProductVariantEntity.ENTITY)
                .where(DiscountToProductVariantEntity.ENTITY.productVariantId.eq(id))
        )
        val appliesProductCondition = DiscountEntity.ENTITY.id.`in`(
            SQLExpressions.select(DiscountToProductEntity.ENTITY.discountId).from(DiscountToProductEntity.ENTITY)
                .join(ProductVariantEntity.ENTITY)
                .on(DiscountToProductEntity.ENTITY.productId.eq(ProductVariantEntity.ENTITY.productId))
                .where(ProductVariantEntity.ENTITY.id.eq(id))
        )
        val appliesCategoryCondition = DiscountEntity.ENTITY.id.`in`(
            SQLExpressions.select(DiscountToCategoryEntity.ENTITY.discountId).from(DiscountToCategoryEntity.ENTITY)
                .join(ProductToCategoryEntity.ENTITY)
                .on(DiscountToCategoryEntity.ENTITY.categoryId.eq(ProductToCategoryEntity.ENTITY.categoryId))
                .join(ProductVariantEntity.ENTITY)
                .on(ProductToCategoryEntity.ENTITY.productId.eq(ProductVariantEntity.ENTITY.productId))
                .where(ProductVariantEntity.ENTITY.id.eq(id))
        )
        return appliesProductVariantCondition.or(appliesProductCondition).or(appliesCategoryCondition)
    }

}