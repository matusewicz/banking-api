package com.github.matusewicz.bankingapi.domain.model

import javax.money.MonetaryAmount

data class Balance(val account: Account, val value: MonetaryAmount)