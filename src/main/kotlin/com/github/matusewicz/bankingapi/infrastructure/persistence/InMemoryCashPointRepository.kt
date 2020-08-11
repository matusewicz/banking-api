package com.github.matusewicz.bankingapi.infrastructure.persistence

import com.github.matusewicz.bankingapi.domain.logic.exceptions.NotFoundException
import com.github.matusewicz.bankingapi.domain.model.CashPoint
import com.github.matusewicz.bankingapi.domain.persistence.CashPointRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryCashPointRepository(
    @Qualifier("cashPointStorage") private val storage: ConcurrentHashMap<String, CashPoint>
) : CashPointRepository {

    private val log = LoggerFactory.getLogger(javaClass)


    override fun getCashPoint(id: String): CashPoint {
        return findCashPoint(id) ?: throw NotFoundException("Cash Point <$id> does not exist.")
    }

    override fun findCashPoint(id: String): CashPoint? {
        return storage[id]
    }

}