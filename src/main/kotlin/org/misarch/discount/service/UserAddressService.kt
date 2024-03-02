package org.misarch.discount.service

import kotlinx.coroutines.reactor.awaitSingle
import org.misarch.discount.event.DiscountEvents
import org.misarch.discount.event.EventPublisher
import org.misarch.discount.event.model.ArchiveUserAddressDTO
import org.misarch.discount.graphql.AuthorizedUser
import org.misarch.discount.graphql.input.ArchiveUserAddressInput
import org.misarch.discount.graphql.input.CreateUserAddressInput
import org.misarch.discount.persistence.model.DiscountEntity
import org.misarch.discount.persistence.repository.AddressRepository
import org.misarch.discount.persistence.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

/**
 * Service for [DiscountEntity]s
 *
 * @param repository the provided repository
 * @param userRepository the user repository
 * @param eventPublisher the event publisher
 */
@Service
class UserAddressService(
    repository: AddressRepository,
    private val userRepository: UserRepository,
    private val eventPublisher: EventPublisher
) : BaseService<DiscountEntity, AddressRepository>(repository) {

    /**
     * Creates a user address
     *
     * @param userAddressInput the user address to create
     * @return the created user address
     */
    suspend fun createUserAddress(userAddressInput: CreateUserAddressInput): DiscountEntity {
        if (!userRepository.existsById(userAddressInput.userId).awaitSingle()) {
            throw IllegalArgumentException("User with id ${userAddressInput.userId} does not exist")
        }
        val userAddress = DiscountEntity(
            street1 = userAddressInput.street1,
            street2 = userAddressInput.street2,
            city = userAddressInput.city,
            postalCode = userAddressInput.postalCode,
            country = userAddressInput.country,
            companyName = userAddressInput.companyName,
            userId = userAddressInput.userId,
            id = null,
            version = null,
            archivedAt = null
        )
        val savedUserAddress = repository.save(userAddress).awaitSingle()
        eventPublisher.publishEvent(DiscountEvents.USER_ADDRESS_CREATED, savedUserAddress.toEventDTO())
        return savedUserAddress
    }

    /**
     * Archives a user address
     * Also checks permissions
     *
     * @param archiveUserAddressInput defines the user address to archive
     * @return the archived user address
     */
    suspend fun archiveUserAddress(archiveUserAddressInput: ArchiveUserAddressInput, authorizedUser: AuthorizedUser): DiscountEntity {
        val userAddress = repository.findById(archiveUserAddressInput.id).awaitSingle()
        if (userAddress.userId != authorizedUser.id) {
            authorizedUser.checkIsEmployee()
        }
        userAddress.archivedAt = OffsetDateTime.now()
        val savedUserAddress = repository.save(userAddress).awaitSingle()
        eventPublisher.publishEvent(DiscountEvents.USER_ADDRESS_ARCHIVED, ArchiveUserAddressDTO(userAddress.id!!))
        return savedUserAddress
    }

}