package com.github.matusewicz.bankingapi.infrastructure.http.balance

import com.github.matusewicz.bankingapi.infrastructure.http.Links
import javax.money.MonetaryAmount

data class BalanceRepresentation(val accountNumber: String, val balance: MonetaryAmount, val _links: Links?)