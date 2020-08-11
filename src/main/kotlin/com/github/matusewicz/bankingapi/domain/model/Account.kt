package com.github.matusewicz.bankingapi.domain.model

import javax.money.CurrencyUnit

interface Account {
    val accountNumber: String
}

data class CustomerAccount(
    override val accountNumber: String,
    val email: String,
    val baseCurrency: CurrencyUnit
) : Account


data class CashPoint(
    override val accountNumber: String
) : Account
