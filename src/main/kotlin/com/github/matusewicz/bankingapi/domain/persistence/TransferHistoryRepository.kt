package com.github.matusewicz.bankingapi.domain.persistence

import com.github.matusewicz.bankingapi.domain.model.MoneyTransfer

interface TransferHistoryRepository {
    fun getAllTransfers(): List<MoneyTransfer>
    fun findTransfer(id: String): MoneyTransfer?
    fun getTransfer(id: String): MoneyTransfer
    fun createTransfer(moneyTransfer: MoneyTransfer)
}