package com.github.matusewicz.bankingapi

import com.github.matusewicz.bankingapi.domain.model.CustomerAccount
import org.javamoney.moneta.Money
import java.math.BigDecimal
import javax.money.CurrencyUnit
import javax.money.Monetary

object TestUtils {
    fun String.toCurrency(): CurrencyUnit = Monetary.getCurrency(this)
    fun Number.euro(): Money = Money.of(BigDecimal(this.toString()), "EUR")
    fun Number.dollar(): Money = Money.of(BigDecimal(this.toString()), "USD")

    val accountAlice = CustomerAccount(
        accountNumber = "alice",
        baseCurrency = "EUR".toCurrency(),
        email = "alice@example.org"
    )
    val accountBob = CustomerAccount(
        accountNumber = "bob",
        baseCurrency = "EUR".toCurrency(),
        email = "bob@example.org"
    )
}