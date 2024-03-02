package org.misarch.discount.service

import kotlinx.coroutines.reactor.awaitSingle
import org.misarch.discount.event.DiscountEvents
import org.misarch.discount.event.EventPublisher
import org.misarch.discount.graphql.input.CreateVendorAddressInput
import org.misarch.discount.persistence.model.DiscountEntity
import org.misarch.discount.persistence.repository.AddressRepository
import org.springframework.stereotype.Service

/**
 * Service for [DiscountEntity]s
 *
 * @param repository the provided repository
 * @param eventPublisher the event publisher
 */
@Service
class VendorAddressService(
    repository: AddressRepository,
    private val eventPublisher: EventPublisher
) : BaseService<DiscountEntity, AddressRepository>(repository) {

    /**
     * Creates a vendor address
     *
     * @param vendorAddressInput the vendor address to create
     * @return the created vendor address
     */
    suspend fun createVendorAddress(vendorAddressInput: CreateVendorAddressInput): DiscountEntity {
        val vendorAddress = DiscountEntity(
            street1 = vendorAddressInput.street1,
            street2 = vendorAddressInput.street2,
            city = vendorAddressInput.city,
            postalCode = vendorAddressInput.postalCode,
            country = vendorAddressInput.country,
            companyName = vendorAddressInput.companyName,
            userId = null,
            id = null,
            version = null,
            archivedAt = null
        )
        val savedVendorAddress = repository.save(vendorAddress).awaitSingle()
        eventPublisher.publishEvent(DiscountEvents.VENDOR_ADDRESS_CREATED, savedVendorAddress.toEventDTO())
        return savedVendorAddress
    }

}