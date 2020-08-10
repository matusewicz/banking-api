package com.github.matusewicz.bankingapi.infrastructure.persistence

import com.github.matusewicz.bankingapi.domain.model.Account
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentHashMap

@Configuration
class PersistenceConfig {

    @Bean("accountStorage")
    fun accountStorage() = ConcurrentHashMap<String, Account>()
}