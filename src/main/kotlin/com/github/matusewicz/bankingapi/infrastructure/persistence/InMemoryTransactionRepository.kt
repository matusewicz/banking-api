package com.github.matusewicz.bankingapi.infrastructure.persistence

import com.github.matusewicz.bankingapi.domain.logic.exceptions.NotFoundException
import com.github.matusewicz.bankingapi.domain.model.CreditTransaction
import com.github.matusewicz.bankingapi.domain.model.DebitTransaction
import com.github.matusewicz.bankingapi.domain.model.Transaction
import com.github.matusewicz.bankingapi.domain.persistence.TransactionRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

typealias AccountNumber = String
typealias TransactionId = String

@Repository
class InMemoryTransactionRepository(@Qualifier("transactionStorage") private val storage: ConcurrentHashMap<AccountNumber, ConcurrentHashMap<TransactionId, Transaction>>) : TransactionRepository {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun getAllTransactions(accountNumber: String): List<Transaction> {
        return storage[accountNumber]?.values?.toList() ?: emptyList()
    }

    override fun findTransaction(accountNumber: String, transactionId: String): Transaction? {
        return storage[accountNumber]?.get(transactionId)
    }

    override fun getTransaction(accountNumber: String, transactionId: String): Transaction {
        return findTransaction(accountNumber, transactionId) ?: throw NotFoundException("Transaction <$transactionId> for account <$accountNumber> does not exist.")
    }

    override fun createTransactions(debitTransaction: DebitTransaction, creditTransaction: CreditTransaction) {
        log.info("Transferring <{}> from Account <{}> to Account <{}>...", creditTransaction.amount, debitTransaction.account.accountNumber, creditTransaction.account.accountNumber)

        val debitAccountTransactions = storage.getOrPut(debitTransaction.account.accountNumber, { ConcurrentHashMap() })
        val creditAccountTransactions = storage.getOrPut(creditTransaction.account.accountNumber, { ConcurrentHashMap() })

        debitAccountTransactions[debitTransaction.id] = debitTransaction
        creditAccountTransactions[creditTransaction.id] = creditTransaction
    }
}