package org.misarch.discount.event.model.order

import java.util.UUID

/**
 * DTO of an order of a user.
 *
 * @property id Order UUID.
 * @property userId UUID of user connected with Order.
 * @property createdAt Timestamp when Order was created.
 * @property orderStatus The status of the Order.
 * @property placedAt Timestamp of Order placement. Not present until Order is placed.
 * @property rejectionReason The rejection reason if status of the Order is REJECTED.
 * @property orderItems List of OrderItems associated with the Order.
 */
data class OrderDTO(
    val id: UUID,
    val userId: UUID,
    val createdAt: String,
    val orderStatus: OrderStatus,
    val placedAt: String?,
    val rejectionReason: RejectionReason,
    val orderItems: List<OrderItemDTO>
)