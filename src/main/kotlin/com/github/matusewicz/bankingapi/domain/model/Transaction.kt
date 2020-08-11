package com.github.matusewicz.bankingapi.domain.model

import javax.money.MonetaryAmount
import javax.validation.constraints.Negative
import javax.validation.constraints.Positive

interface Transaction {
    val id: String
    val account: Account
    val amount: MonetaryAmount
}

data class DebitTransaction(
    override val id: String,
    override val account: Account,
    @get: Negative override val amount: MonetaryAmount
) : Transaction

data class CreditTransaction(
    override val id: String,
    override val account: Account,
    @get: Positive override val amount: MonetaryAmount
) : Transaction