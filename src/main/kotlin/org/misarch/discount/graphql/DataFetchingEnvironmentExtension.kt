package org.misarch.discount.graphql

import graphql.schema.DataFetchingEnvironment

/**
 * Extension function to retrieve the authorized user from the [DataFetchingEnvironment].
 *
 * @return the authorized user or null if not present
 */
val DataFetchingEnvironment.authorizedUserOrNull: AuthorizedUser?
    get() = this.graphQlContext.getOrDefault<AuthorizedUser?>(AuthorizedUser::class, null)

/**
 * Extension function to retrieve the authorized user from the [DataFetchingEnvironment].
 *
 * @return the authorized user
 */
val DataFetchingEnvironment.authorizedUser: AuthorizedUser
    get() = this.authorizedUserOrNull ?: throw IllegalStateException("Unauthorized access")