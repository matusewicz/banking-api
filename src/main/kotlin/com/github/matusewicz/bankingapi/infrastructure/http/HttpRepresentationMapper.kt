package com.github.matusewicz.bankingapi.infrastructure.http

import com.github.matusewicz.bankingapi.domain.model.Account
import com.github.matusewicz.bankingapi.infrastructure.http.account.AccountListRepresentation
import com.github.matusewicz.bankingapi.infrastructure.http.account.AccountRepresentation
import org.springframework.stereotype.Service

@Service
class HttpRepresentationMapper(private val linkBuilder: LinkBuilder) {

    fun map(account: Account): AccountRepresentation {
        return AccountRepresentation(
            accountNumber = account.accountNumber,
            email = account.email,
            baseCurrency = account.baseCurrency,
            _links = mapOf(
                "_self" to linkBuilder.accountLink(account.accountNumber),
                "accounts" to linkBuilder.accountListLink()
            )
        )
    }

    fun map(accounts: List<Account>): AccountListRepresentation {
        return AccountListRepresentation(
            accounts = accounts.map { account ->
                map(account).copy(
                    _links = mapOf(
                        "_self" to linkBuilder.accountLink(account.accountNumber)
                    )
                )
            },
            _links = mapOf("_self" to linkBuilder.accountListLink())
        )
    }
}