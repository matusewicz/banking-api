package com.github.matusewicz.bankingapi.domain.persistence

import com.github.matusewicz.bankingapi.domain.model.CustomerAccount
import javax.money.CurrencyUnit

interface CustomerAccountRepository {
    fun createAccount(currency: CurrencyUnit, email: String): CustomerAccount
    fun getAllAccounts(): List<CustomerAccount>
    fun getAccount(id: String): CustomerAccount
    fun findAccount(id: String): CustomerAccount?
    fun lock(account: CustomerAccount)
    fun unlock(account: CustomerAccount)
}