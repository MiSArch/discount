package org.misarch.discount

import com.infobip.spring.data.r2dbc.EnableQuerydslR2dbcRepositories
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableQuerydslR2dbcRepositories
class AddressApplication

fun main(args: Array<String>) {
    runApplication<AddressApplication>(*args)
}