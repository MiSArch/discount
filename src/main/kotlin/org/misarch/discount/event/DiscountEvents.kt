package org.misarch.discount.event

/**
 * Constants for address event topics used in the application
 */
object DiscountEvents {
    /**
     * Topic for user creation events (a user has been created)
     */
    const val USER_CREATED = "user/user/created"

    /**
     * Topic for discount creation events (a discount has been created)
     */
    const val DISCOUNT_CREATED = "discount/discount/created"

    /**
     * Topic for coupon creation events (a coupon has been created)
     */
    const val COUPON_CREATED = "discount/coupon/created"

    /**
     * Name of the pubsub component
     */
    const val PUBSUB_NAME = "pubsub"
}