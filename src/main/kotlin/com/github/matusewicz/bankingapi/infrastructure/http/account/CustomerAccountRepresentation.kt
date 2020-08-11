package com.github.matusewicz.bankingapi.infrastructure.http.account

import com.github.matusewicz.bankingapi.infrastructure.http.Links
import javax.money.CurrencyUnit

data class CustomerAccountRepresentation(val accountNumber: String, val email: String, val baseCurrency: CurrencyUnit, val _links: Links)