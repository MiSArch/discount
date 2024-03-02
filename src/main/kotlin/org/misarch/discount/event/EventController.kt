package org.misarch.discount.event

import io.dapr.Topic
import io.dapr.client.domain.CloudEvent
import org.misarch.discount.event.model.UserDTO
import org.misarch.discount.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * Controller for dapr events
 *
 * @param userService the user service
 */
@Controller
class EventController(
    private val userService: UserService
) {

    /**
     * Handles a user created event
     *
     * @param cloudEvent the cloud event containing the user created
     */
    @Topic(name = DiscountEvents.USER_CREATED, pubsubName = DiscountEvents.PUBSUB_NAME)
    @PostMapping("/subscription/${DiscountEvents.USER_CREATED}")
    @ResponseStatus(code = HttpStatus.OK)
    suspend fun onUserCreated(
        @RequestBody
        cloudEvent: CloudEvent<UserDTO>
    ) {
        userService.registerUser(cloudEvent.data)
    }

}