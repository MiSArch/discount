package org.misarch.discount.graphql

import java.util.*

/**
 * Represents an authorized user.
 *
 * @param id unique identifier of the user
 * @param roles list of roles assigned to the user
 */
data class AuthorizedUser(
    val id: UUID,
    val roles: List<String>
) {

    /**
     * Check if the user is an employee or admin.
     */
    val isEmployee: Boolean
        get() = roles.contains("employee") || isAdmin


    /**
     * Check if the user is an admin.
     */
    val isAdmin: Boolean
        get() = roles.contains("admin")

    /**
     * Check if the user is an admin and throw an exception if not.
     *
     * @throws IllegalStateException if the user is not an admin
     */
    fun checkIsAdmin() {
        if (!isAdmin) {
            throw IllegalStateException("Unauthorized access: $id is not an admin")
        }
    }

    /**
     * Check if the user is an employee and throw an exception if not.
     *
     * @throws IllegalStateException if the user is not an employee
     */
    fun checkIsEmployee() {
        if (!isEmployee) {
            throw IllegalStateException("Unauthorized access: $id is not an employee or admin")
        }
    }

}
