package com.github.matusewicz.bankingapi.infrastructure.http.transaction

import com.github.matusewicz.bankingapi.infrastructure.http.Links

data class TransactionListRepresentation(val transactions: List<TransactionRepresentation>, val _links: Links? = null)