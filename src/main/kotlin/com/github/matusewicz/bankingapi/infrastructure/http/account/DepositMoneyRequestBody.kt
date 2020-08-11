package com.github.matusewicz.bankingapi.infrastructure.http.account

import javax.money.MonetaryAmount
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

data class DepositMoneyRequestBody(
    @get: NotBlank val cashPointId: String,
    @get: Positive val depositAmount: MonetaryAmount
)