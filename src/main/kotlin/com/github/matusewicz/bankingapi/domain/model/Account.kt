package com.github.matusewicz.bankingapi.domain.model

import javax.money.CurrencyUnit

data class Account(val accountNumber: String, val email: String, val baseCurrency: CurrencyUnit)