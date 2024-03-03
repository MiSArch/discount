package org.misarch.discount.service

import org.misarch.discount.event.model.ProductVariantVersionDTO
import org.misarch.discount.persistence.model.ProductVariantVersionEntity
import org.misarch.discount.persistence.repository.ProductVariantVersionRepository
import org.springframework.stereotype.Service

/**
 * Service for [ProductVariantVersionEntity]s
 *
 * @param repository the repository for [ProductVariantVersionEntity]s
 */
@Service
class ProductVariantVersionService(
    repository: ProductVariantVersionRepository
) : BaseService<ProductVariantVersionEntity, ProductVariantVersionRepository>(repository) {

    /**
     * Registers a productVariantVersion
     *
     * @param productVariantVersionDTO the productVariantVersion to register
     */
    suspend fun registerProductVariantVersion(productVariantVersionDTO: ProductVariantVersionDTO) {
        repository.createProductVariantVersion(productVariantVersionDTO.id, productVariantVersionDTO.productVariantId)
    }

}