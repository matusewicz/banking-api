package com.github.matusewicz.bankingapi.infrastructure.http

import com.github.matusewicz.bankingapi.domain.model.CustomerAccount
import com.github.matusewicz.bankingapi.domain.model.Transaction
import com.github.matusewicz.bankingapi.infrastructure.http.account.CustomerAccountListRepresentation
import com.github.matusewicz.bankingapi.infrastructure.http.account.CustomerAccountRepresentation
import com.github.matusewicz.bankingapi.infrastructure.http.transaction.TransactionListRepresentation
import com.github.matusewicz.bankingapi.infrastructure.http.transaction.TransactionRepresentation
import org.springframework.stereotype.Service

@Service
class HttpRepresentationMapper(private val linkBuilder: LinkBuilder) {

    fun map(account: CustomerAccount): CustomerAccountRepresentation {
        return CustomerAccountRepresentation(
            accountNumber = account.accountNumber,
            email = account.email,
            baseCurrency = account.baseCurrency,
            _links = mapOf(
                "_self" to linkBuilder.accountLink(account.accountNumber),
                "accounts" to linkBuilder.accountListLink(),
                "transactions" to linkBuilder.transactionListLink(account.accountNumber)
            )
        )
    }

    fun map(accounts: List<CustomerAccount>): CustomerAccountListRepresentation {
        return CustomerAccountListRepresentation(
            customerAccounts = accounts.map { account ->
                map(account).copy(
                    _links = mapOf(
                        "_self" to linkBuilder.accountLink(account.accountNumber)
                    )
                )
            },
            _links = mapOf("_self" to linkBuilder.accountListLink())
        )
    }

    fun map(transaction: Transaction): TransactionRepresentation {
        return TransactionRepresentation(
            id = transaction.id,
            transactionAmount = transaction.amount,
            _links = mapOf(
                "_self" to linkBuilder.transactionLink(transaction),
                "transactions" to linkBuilder.transactionListLink(transaction.account.accountNumber)
            )
        )
    }

    fun map(accountId: String, transactions: List<Transaction>): TransactionListRepresentation {
        return TransactionListRepresentation(
            transactions = transactions.map { transaction ->
                map(transaction).copy(
                    _links = mapOf(
                        "_self" to linkBuilder.transactionLink(transaction)
                    )
                )
            },
            _links = mapOf(
                "_self" to linkBuilder.transactionListLink(accountId),
                "account" to linkBuilder.accountLink(accountId)
            )
        )
    }
}