package com.github.matusewicz.bankingapi.infrastructure.persistence

import com.github.matusewicz.bankingapi.domain.persistence.IDGenerator
import org.springframework.stereotype.Service
import java.util.*

@Service
class UUIDGenerator : IDGenerator {
    override fun newAccountNumber() = UUID.randomUUID().toString()
}