package com.github.matusewicz.bankingapi.infrastructure.persistence

import com.github.matusewicz.bankingapi.domain.logic.exceptions.NotFoundException
import com.github.matusewicz.bankingapi.domain.model.MoneyTransfer
import com.github.matusewicz.bankingapi.domain.persistence.TransferHistoryRepository
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryTransferHistoryRepository(@Qualifier("transferStorage" ) private val storage: ConcurrentHashMap<String, MoneyTransfer>) :
    TransferHistoryRepository {

    override fun getAllTransfers(): List<MoneyTransfer> {
        return storage.values.toList()
    }

    override fun findTransfer(id: String): MoneyTransfer? {
        return storage[id]
    }

    override fun getTransfer(id: String): MoneyTransfer {
        return findTransfer(id) ?: throw NotFoundException("Money transfer <$id> does not exist.")
    }

    override fun createTransfer(moneyTransfer: MoneyTransfer) {
        storage[moneyTransfer.id] = moneyTransfer
    }
}