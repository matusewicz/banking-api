package com.github.matusewicz.bankingapi.domain.model

import javax.money.MonetaryAmount

interface Transaction {
    val id: String
    val account: Account
    val amount: MonetaryAmount
}

data class DebitTransaction(
    override val id: String,
    override val account: Account,
    override val amount: MonetaryAmount
) : Transaction

data class CreditTransaction(
    override val id: String,
    override val account: Account,
    override val amount: MonetaryAmount
) : Transaction