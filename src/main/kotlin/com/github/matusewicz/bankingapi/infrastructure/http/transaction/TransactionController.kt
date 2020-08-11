package com.github.matusewicz.bankingapi.infrastructure.http.transaction

import com.github.matusewicz.bankingapi.domain.persistence.TransactionRepository
import com.github.matusewicz.bankingapi.infrastructure.http.HttpRepresentationMapper
import com.github.matusewicz.bankingapi.infrastructure.http.account.AccountController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/${AccountController.PATH}/{accountNumber}/${TransactionController.PATH}")
class TransactionController(
    private val transactionRepository: TransactionRepository,
    private val mapper: HttpRepresentationMapper
) {

    companion object {
        const val PATH = "transactions"
    }

    @GetMapping
    fun getAllTransactions(@PathVariable accountNumber: String): TransactionListRepresentation {
        val transactions = transactionRepository.getAllTransactions(accountNumber)

        return mapper.map(accountNumber, transactions)
    }

    @GetMapping("/{transactionId}")
    fun getTransaction(@PathVariable accountNumber: String, @PathVariable transactionId: String): TransactionRepresentation {
        val transaction = transactionRepository.getTransaction(accountNumber = accountNumber, transactionId = transactionId)

        return mapper.map(transaction)
    }
}