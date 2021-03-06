package com.github.matusewicz.bankingapi.infrastructure.http.account

import com.github.matusewicz.bankingapi.infrastructure.http.Links
import javax.money.CurrencyUnit

data class AccountRepresentation(val accountNumber: String, val _links: Links)