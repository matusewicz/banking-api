package com.github.matusewicz.bankingapi.infrastructure.http.transaction

import com.github.matusewicz.bankingapi.infrastructure.http.Links
import javax.money.MonetaryAmount

data class TransactionRepresentation(
    val id: String,
    val transactionAmount: MonetaryAmount,
    val _links: Links? = null
)