package com.github.matusewicz.bankingapi.infrastructure.http

import com.github.matusewicz.bankingapi.infrastructure.http.account.AccountController
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class LinkBuilder(@Value("\${application.base-url}") private val baseUrl: String) {

    fun absoluteUrl(relative: String): String {
        return "$baseUrl/$relative"
    }

    fun accountListLink() = Link(absoluteUrl(AccountController.PATH))
    fun accountLink(accountId: String) = Link(absoluteUrl("${AccountController.PATH}/$accountId"))
}