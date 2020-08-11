package com.github.matusewicz.bankingapi.domain.model

data class MoneyTransfer(
    val id: String,
    val debitTransaction: DebitTransaction,
    val creditTransaction: CreditTransaction,
    val reference: String
)