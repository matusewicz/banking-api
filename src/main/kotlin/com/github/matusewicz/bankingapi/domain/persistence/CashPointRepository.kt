package com.github.matusewicz.bankingapi.domain.persistence

import com.github.matusewicz.bankingapi.domain.model.CashPoint

interface CashPointRepository {
    fun getCashPoint(id: String): CashPoint
    fun findCashPoint(id: String): CashPoint?
}