package com.github.matusewicz.bankingapi.infrastructure.http.account

import javax.money.MonetaryAmount

data class DepositMoneyRequestBody(
    val cashPointId: String,
    val depositAmount: MonetaryAmount
)