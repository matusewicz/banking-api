package com.github.matusewicz.bankingapi.domain.persistence

/** Contract: Implementation has to guarantee that generated ids are globally unique   */
interface IDGenerator {
    fun newAccountNumber(): String
    fun newTransactionId(): String
}