package com.github.matusewicz.bankingapi.infrastructure.http.transfer

import com.github.matusewicz.bankingapi.infrastructure.http.Links
import com.github.matusewicz.bankingapi.infrastructure.http.transaction.TransactionRepresentation

data class MoneyTransferRepresentation(val id: String, val debitTransaction: TransactionRepresentation, val creditTransaction: TransactionRepresentation, val _links: Links?)