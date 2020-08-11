package com.github.matusewicz.bankingapi.infrastructure.http.transfer

import com.github.matusewicz.bankingapi.infrastructure.http.DebtorAndCreditorAreNotSame
import javax.money.MonetaryAmount
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

@DebtorAndCreditorAreNotSame
data class MoneyTransferRequestBody(
    @get:NotBlank val debtorAccountId: String,
    @get:NotBlank val creditorAccountId: String,
    @get:Positive val instructedAmount: MonetaryAmount,
    @get:NotBlank val reference: String
)