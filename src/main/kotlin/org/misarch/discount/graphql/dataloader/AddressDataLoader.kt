package org.misarch.discount.graphql.dataloader

import org.misarch.discount.graphql.model.Discount
import org.misarch.discount.graphql.model.UserAddress
import org.misarch.discount.persistence.model.DiscountEntity
import org.misarch.discount.persistence.repository.AddressRepository
import org.springframework.stereotype.Component

/**
 * Data loader for [UserAddress]s
 *
 * @param repository repository for [DiscountEntity]s
 */
@Component
class AddressDataLoader(
    repository: AddressRepository
) : IdDataLoader<Discount, DiscountEntity>(repository)