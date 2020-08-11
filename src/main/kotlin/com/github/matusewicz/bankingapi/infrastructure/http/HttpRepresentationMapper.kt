package com.github.matusewicz.bankingapi.infrastructure.http

import com.github.matusewicz.bankingapi.domain.model.Balance
import com.github.matusewicz.bankingapi.domain.model.CustomerAccount
import com.github.matusewicz.bankingapi.domain.model.MoneyTransfer
import com.github.matusewicz.bankingapi.domain.model.Transaction
import com.github.matusewicz.bankingapi.infrastructure.http.account.CustomerAccountListRepresentation
import com.github.matusewicz.bankingapi.infrastructure.http.account.CustomerAccountRepresentation
import com.github.matusewicz.bankingapi.infrastructure.http.balance.BalanceRepresentation
import com.github.matusewicz.bankingapi.infrastructure.http.transaction.TransactionListRepresentation
import com.github.matusewicz.bankingapi.infrastructure.http.transaction.TransactionRepresentation
import com.github.matusewicz.bankingapi.infrastructure.http.transfer.MoneyTransferListRepresentation
import com.github.matusewicz.bankingapi.infrastructure.http.transfer.MoneyTransferRepresentation
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
                "balance" to linkBuilder.balanceLink(account.accountNumber),
                "transactions" to linkBuilder.transactionListLink(account.accountNumber)
            )
        )
    }

    fun map(accounts: List<CustomerAccount>): CustomerAccountListRepresentation {
        return CustomerAccountListRepresentation(
            customerAccounts = accounts.map { account ->
                map(account).copy(
                    _links = mapOf(
                        "_self" to linkBuilder.accountLink(account.accountNumber),
                        "balance" to linkBuilder.balanceLink(account.accountNumber),
                        "transactions" to linkBuilder.transactionListLink(account.accountNumber)
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

    fun map(balance: Balance): BalanceRepresentation {
        return BalanceRepresentation(
            accountNumber = balance.account.accountNumber,
            balance = balance.value,
            _links = mapOf(
                "_self" to linkBuilder.balanceLink(balance.account.accountNumber),
                "account" to linkBuilder.accountLink(balance.account.accountNumber)
            )
        )
    }

    fun map(moneyTransfer: MoneyTransfer): MoneyTransferRepresentation {
        return MoneyTransferRepresentation(
            id = moneyTransfer.id,
            debitTransaction = map(moneyTransfer.debitTransaction),
            creditTransaction = map(moneyTransfer.creditTransaction),
            _links = mapOf(
                "_self" to linkBuilder.transferLink(moneyTransfer)
            )
        )
    }

    fun map(moneyTransfers: List<MoneyTransfer>): MoneyTransferListRepresentation {
        return MoneyTransferListRepresentation(
            transfers = moneyTransfers.map { moneyTransfer ->
                map(moneyTransfer)
            },
            _links = mapOf("_self" to linkBuilder.transferListLink())
        )
    }
}