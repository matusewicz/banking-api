package com.github.matusewicz.bankingapi.infrastructure.http

import com.github.matusewicz.bankingapi.domain.model.CustomerAccount
import com.github.matusewicz.bankingapi.infrastructure.http.account.CustomerAccountRepresentation
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.money.Monetary

class HttpRepresentationMapperTest {

    val unit = HttpRepresentationMapper(LinkBuilder(baseUrl = "https://example.org"))

    @Test
    fun `should map account to http resource representation`() {
        val account = CustomerAccount(
            accountNumber = "alice",
            email = "alice@example.org",
            baseCurrency = Monetary.getCurrency("EUR")
        )

        val representation = unit.map(account)

        assertThat(representation.accountNumber).isEqualTo(account.accountNumber)
        assertThat(representation.email).isEqualTo(account.email)
        assertThat(representation.baseCurrency).isEqualTo(account.baseCurrency)
    }

    @Test
    fun `should include hateoas links in http resource`() {
        val account = CustomerAccount(
            accountNumber = "alice",
            email = "alice@example.org",
            baseCurrency = Monetary.getCurrency("EUR")
        )

        val representation = unit.map(account)

        assertThat(representation._links).hasSize(3)
        assertThat(representation._links["_self"]).isEqualTo(Link("https://example.org/accounts/${account.accountNumber}"))
        assertThat(representation._links["accounts"]).isEqualTo(Link("https://example.org/accounts"))
        assertThat(representation._links["transactions"]).isEqualTo(Link("https://example.org/accounts/${account.accountNumber}/transactions"))
    }

    @Test
    fun `should map list of accounts to http resource representation`() {
        val oneAccount = CustomerAccount(
            accountNumber = "alice",
            email = "alice@example.org",
            baseCurrency = Monetary.getCurrency("EUR")
        )
        val anotherAccount =
            CustomerAccount(
                accountNumber = "bob",
                email = "bob@example.org",
                baseCurrency = Monetary.getCurrency("EUR")
            )

        val listRepresentation = unit.map(listOf(oneAccount, anotherAccount))

        assertThat(listRepresentation.customerAccounts).hasSize(2)
        assertThat(listRepresentation.customerAccounts).containsExactlyInAnyOrder(
            CustomerAccountRepresentation(
                accountNumber = oneAccount.accountNumber,
                baseCurrency = oneAccount.baseCurrency,
                email = oneAccount.email,
                _links = mapOf(
                    "_self" to Link("https://example.org/accounts/${oneAccount.accountNumber}")
                )
            ),
            CustomerAccountRepresentation(
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