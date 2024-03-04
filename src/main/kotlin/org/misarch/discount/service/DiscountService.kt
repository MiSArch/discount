package org.misarch.discount.service

import kotlinx.coroutines.reactor.awaitSingle
import org.misarch.discount.event.DiscountEvents
import org.misarch.discount.event.EventPublisher
import org.misarch.discount.event.model.ValidationFailedDTO
import org.misarch.discount.event.model.ValidationSucceededDTO
import org.misarch.discount.event.model.order.OrderDTO
import org.misarch.discount.graphql.input.CreateDiscountInput
import org.misarch.discount.persistence.model.DiscountEntity
import org.misarch.discount.persistence.model.DiscountToCategoryEntity
import org.misarch.discount.persistence.model.DiscountToProductEntity
import org.misarch.discount.persistence.model.DiscountToProductVariantEntity
import org.misarch.discount.persistence.repository.*
import org.springframework.stereotype.Service
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

}