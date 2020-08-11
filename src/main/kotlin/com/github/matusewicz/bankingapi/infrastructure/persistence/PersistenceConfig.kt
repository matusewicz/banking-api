package com.github.matusewicz.bankingapi.infrastructure.persistence

import com.github.matusewicz.bankingapi.domain.model.CashPoint
import com.github.matusewicz.bankingapi.domain.model.CustomerAccount
import com.github.matusewicz.bankingapi.domain.model.Transaction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentHashMap

@Configuration
class PersistenceConfig {

    @Bean("accountStorage")
    fun accountStorage() = ConcurrentHashMap<String, CustomerAccount>()

    @Bean("cashPointStorage")
    fun cashPointStorage() = ConcurrentHashMap(
        // pre-configure some cash points to allow customer to deposit money
        mapOf(
            "CP-1" to CashPoint("1111"),
            "CP-2" to CashPoint("2222")
        )
    )

    @Bean("transactionStorage")
    fun transactionStorage() = ConcurrentHashMap<AccountNumber, ConcurrentHashMap<TransactionId, Transaction>>()
}