package org.misarch.discount.event

/**
 * Constants for discount event topics used in the application
 */
object DiscountEvents {
    /**
     * Topic for discount creation events (a discount has been created)
     */
    const val DISCOUNT_CREATED = "discount/discount/created"

    /**
     * Topic for coupon creation events (a coupon has been created)
     */
    const val COUPON_CREATED = "discount/coupon/created"

    /**
     * Topic for user creation events (a user has been created)
     */
    const val USER_CREATED = "user/user/created"

    /**
     * Topic for product creation events (a product has been created)
     */
    const val PRODUCT_CREATED = "catalog/product/created"

    /**
     * Topic for product variant creation events (a product variant has been created)
     */
    const val PRODUCT_VARIANT_CREATED = "catalog/product-variant/created"

    /**
     * Topic for product variant version creation events (a product variant version has been created)
     */
    const val PRODUCT_VARIANT_VERSION_CREATED = "catalog/product-variant-version/created"

    /**
     * Topic for category creation events (a category has been created)
     */
    const val CATEGORY_CREATED = "catalog/category/created"

    /**
     * Name of the pubsub component
     */
    const val PUBSUB_NAME = "pubsub"
}