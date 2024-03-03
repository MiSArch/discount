package org.misarch.discount.event

import io.dapr.Topic
import io.dapr.client.domain.CloudEvent
import org.misarch.discount.event.model.*
import org.misarch.discount.service.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Controller for dapr events
 *
 * @param userService the user service
 * @param productService the product service
 * @param categoryService the category service
 * @param productVariantService the product variant service
 * @param productVariantVersionService the product variant version service
 */
@Controller
class EventController(
    private val userService: UserService,
    private val productService: ProductService,
    private val categoryService: CategoryService,
    private val productVariantService: ProductVariantService,
    private val productVariantVersionService: ProductVariantVersionService
) {

    /**
     * Handles a user created event
     *
     * @param cloudEvent the cloud event containing the user created
     */
    @Topic(name = DiscountEvents.USER_CREATED, pubsubName = DiscountEvents.PUBSUB_NAME)
    @PostMapping("/subscription/${DiscountEvents.USER_CREATED}")
    @ResponseStatus(code = HttpStatus.OK)
    suspend fun onUserCreated(
        @RequestBody
        cloudEvent: CloudEvent<UserDTO>
    ) {
        userService.registerUser(cloudEvent.data)
    }

    /**
     * Handles a product created event
     *
     * @param cloudEvent the cloud event containing the product created
     */
    @Topic(name = DiscountEvents.PRODUCT_CREATED, pubsubName = DiscountEvents.PUBSUB_NAME)
    @PostMapping("/subscription/${DiscountEvents.PRODUCT_CREATED}")
    @ResponseStatus(code = HttpStatus.OK)
    suspend fun onProductCreated(
        @RequestBody
        cloudEvent: CloudEvent<ProductDTO>
    ) {
        productService.registerProduct(cloudEvent.data)
    }

    /**
     * Handles a category created event
     *
     * @param cloudEvent the cloud event containing the category created
     */
    @Topic(name = DiscountEvents.CATEGORY_CREATED, pubsubName = DiscountEvents.PUBSUB_NAME)
    @PostMapping("/subscription/${DiscountEvents.CATEGORY_CREATED}")
    @ResponseStatus(code = HttpStatus.OK)
    suspend fun onCategoryCreated(
        @RequestBody
        cloudEvent: CloudEvent<CategoryDTO>
    ) {
        categoryService.registerCategory(cloudEvent.data)
    }

    /**
     * Handles a product variant created event
     *
     * @param cloudEvent the cloud event containing the product variant created
     */
    @Topic(name = DiscountEvents.PRODUCT_VARIANT_CREATED, pubsubName = DiscountEvents.PUBSUB_NAME)
    @PostMapping("/subscription/${DiscountEvents.PRODUCT_VARIANT_CREATED}")
    @ResponseStatus(code = HttpStatus.OK)
    suspend fun onProductVariantCreated(
        @RequestBody
        cloudEvent: CloudEvent<ProductVariantDTO>
    ) {
        productVariantService.registerProductVariant(cloudEvent.data)
    }

    /**
     * Handles a product variant version created event
     *
     * @param cloudEvent the cloud event containing the product variant version created
     */
    @Topic(name = DiscountEvents.PRODUCT_VARIANT_VERSION_CREATED, pubsubName = DiscountEvents.PUBSUB_NAME)
    @PostMapping("/subscription/${DiscountEvents.PRODUCT_VARIANT_VERSION_CREATED}")
    @ResponseStatus(code = HttpStatus.OK)
    suspend fun onProductVariantVersionCreated(
        @RequestBody
        cloudEvent: CloudEvent<ProductVariantVersionDTO>
    ) {
        productVariantVersionService.registerProductVariantVersion(cloudEvent.data)
    }

}