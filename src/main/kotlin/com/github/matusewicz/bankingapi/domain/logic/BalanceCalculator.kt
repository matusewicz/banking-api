package com.github.matusewicz.bankingapi.domain.logic

import com.github.matusewicz.bankingapi.domain.model.Balance
import com.github.matusewicz.bankingapi.domain.model.Transaction
import com.github.matusewicz.bankingapi.domain.persistence.CustomerAccountRepository
import com.github.matusewicz.bankingapi.domain.persistence.TransactionRepository
import org.springframework.stereotype.Service
import javax.money.CurrencyUnit
import javax.money.Monetary
import javax.money.MonetaryAmount

@Service
class BalanceCalculator(
    private val accountRepository: CustomerAccountRepository,
    private val transactionRepository: TransactionRepository
) {
    fun calculateBalance(accountId: String): Balance {
        val account = accountRepository.getAccount(accountId)
        val transactions = transactionRepository.getAllTransactions(accountId)
        val balance = aggregateBalance(account.baseCurrency, transactions)

        return Balance(account, balance)
    }

    private fun aggregateBalance(accountCurrencyUnit: CurrencyUnit, transactions: List<Transaction>): MonetaryAmount {
        val zero = Monetary.getDefaultAmountFactory()
            .setNumber(0)
            .setCurrency(accountCurrencyUnit)
            .create()

        return transactions.map { it.amount }.fold(zero, MonetaryAmount::add)
    }
}