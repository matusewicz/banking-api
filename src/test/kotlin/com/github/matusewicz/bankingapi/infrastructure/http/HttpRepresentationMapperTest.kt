package com.github.matusewicz.bankingapi.infrastructure.http

import com.github.matusewicz.bankingapi.domain.model.Account
import com.github.matusewicz.bankingapi.infrastructure.http.account.AccountRepresentation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.money.Monetary

class HttpRepresentationMapperTest {

    val unit = HttpRepresentationMapper(LinkBuilder(baseUrl = "https://example.org"))

    @Test
    fun `should map account to http resource representation`() {
        val account = Account(accountNumber = "alice", email = "alice@example.org", baseCurrency = Monetary.getCurrency("EUR"))

        val representation = unit.map(account)

        assertThat(representation.accountNumber).isEqualTo(account.accountNumber)
        assertThat(representation.email).isEqualTo(account.email)
        assertThat(representation.baseCurrency).isEqualTo(account.baseCurrency)
    }

    @Test
    fun `should include hateoas links in http resource`() {
        val account = Account(accountNumber = "alice", email = "alice@example.org", baseCurrency = Monetary.getCurrency("EUR"))

        val representation = unit.map(account)

        assertThat(representation._links).hasSize(2)
        assertThat(representation._links["_self"]).isEqualTo(Link("https://example.org/accounts/${account.accountNumber}"))
    }

    @Test
    fun `should map list of accounts to http resource representation`() {
        val oneAccount = Account(accountNumber = "alice", email = "alice@example.org", baseCurrency = Monetary.getCurrency("EUR"))
        val anotherAccount = Account(accountNumber = "bob", email = "bob@example.org", baseCurrency = Monetary.getCurrency("EUR"))

        val listRepresentation = unit.map(listOf(oneAccount, anotherAccount))

        assertThat(listRepresentation.accounts).hasSize(2)
        assertThat(listRepresentation.accounts).containsExactlyInAnyOrder(
            AccountRepresentation(
                accountNumber = oneAccount.accountNumber,
                baseCurrency = oneAccount.baseCurrency,
                email = oneAccount.email,
                _links = mapOf(
                    "_self" to Link("https://example.org/accounts/${oneAccount.accountNumber}")
                )
            ),
            AccountRepresentation(
                accountNumber = anotherAccount.accountNumber,
                baseCurrency = anotherAccount.baseCurrency,
                email = anotherAccount.email,
                _links = mapOf(
                    "_self" to Link("https://example.org/accounts/${anotherAccount.accountNumber}")
                )
            )
        )
        assertThat(listRepresentation._links["_self"]).isEqualTo(Link("https://example.org/accounts"))
    }
}