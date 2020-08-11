package com.github.matusewicz.bankingapi.domain.persistence

import com.github.matusewicz.bankingapi.domain.model.CreditTransaction
import com.github.matusewicz.bankingapi.domain.model.DebitTransaction
import com.github.matusewicz.bankingapi.domain.model.Transaction

interface TransactionRepository {
    fun getAllTransactions(accountNumber: String): List<Transaction>
    fun findTransaction(accountNumber: String, transactionId: String): Transaction?
    fun getTransaction(accountNumber: String, transactionId: String): Transaction
    fun createTransactions(debitTransaction: DebitTransaction, creditTransaction: CreditTransaction)
}