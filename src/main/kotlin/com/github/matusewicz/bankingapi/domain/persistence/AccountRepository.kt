package com.github.matusewicz.bankingapi.domain.persistence

import com.github.matusewicz.bankingapi.domain.model.Account
import javax.money.CurrencyUnit

interface AccountRepository {
    fun createAccount(currency: CurrencyUnit, email: String): Account
    fun getAllAccounts(): List<Account>
    fun getAccount(id: String): Account
    fun findAccount(id: String): Account?
}