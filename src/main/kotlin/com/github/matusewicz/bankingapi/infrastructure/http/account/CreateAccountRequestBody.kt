package com.github.matusewicz.bankingapi.infrastructure.http.account

import com.github.matusewicz.bankingapi.infrastructure.http.CurrencyCode
import javax.validation.constraints.Email

data class CreateAccountRequestBody(@get:Email val email: String, @get:CurrencyCode val baseCurrency: String)