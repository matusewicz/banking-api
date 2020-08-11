package com.github.matusewicz.bankingapi.infrastructure.http

import com.github.matusewicz.bankingapi.domain.model.MoneyTransfer
import com.github.matusewicz.bankingapi.domain.model.Transaction
import com.github.matusewicz.bankingapi.infrastructure.http.account.AccountController
import com.github.matusewicz.bankingapi.infrastructure.http.balance.BalanceController
import com.github.matusewicz.bankingapi.infrastructure.http.transaction.TransactionController
import com.github.matusewicz.bankingapi.infrastructure.http.transfer.MoneyTransferController
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class LinkBuilder(@Value("\${application.base-url}") private val baseUrl: String) {

    fun absoluteUrl(relative: String): String {
        return "$baseUrl/$relative"
    }

    fun accountListLink() = Link(absoluteUrl(AccountController.PATH))
    fun accountLink(accountId: String) = Link(absoluteUrl("${AccountController.PATH}/$accountId"))
    fun transferListLink() = Link(absoluteUrl(MoneyTransferController.PATH))
    fun transferLink(moneyTransfer: MoneyTransfer) = Link(absoluteUrl("${MoneyTransferController.PATH}/${moneyTransfer.id}"))
    fun balanceLink(accountId: String) = Link(absoluteUrl("${AccountController.PATH}/$accountId/${BalanceController.PATH}"))
    fun transactionListLink(accountId: String) = Link(absoluteUrl("${AccountController.PATH}/$accountId/${TransactionController.PATH}"))
    fun transactionLink(transaction: Transaction) = Link(absoluteUrl("${AccountController.PATH}/${transaction.account.accountNumber}/${TransactionController.PATH}/${transaction.id}"))
}
