package org.misarch.discount.event.model.order

import java.util.UUID

/**
 * DTO of a shipment associated with one or more order items.
 *
 * @property status Shipment status of the shipment.
 * @property shipmentMethodId UUID of method/provider, which is used for shipping.
 */
data class ShipmentDTO(
    val status: ShipmentStatus,
    val shipmentMethodId: UUID
)