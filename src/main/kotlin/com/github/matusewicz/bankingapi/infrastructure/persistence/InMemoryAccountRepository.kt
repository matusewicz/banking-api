package com.github.matusewicz.bankingapi.infrastructure.persistence

import com.github.matusewicz.bankingapi.domain.model.Account
import com.github.matusewicz.bankingapi.domain.persistence.AccountRepository
import com.github.matusewicz.bankingapi.domain.persistence.IDGenerator
import com.github.matusewicz.bankingapi.domain.logic.exceptions.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import javax.money.CurrencyUnit

@Repository
class InMemoryAccountRepository(
    @Qualifier("accountStorage")
    private val storage: ConcurrentHashMap<String, Account>,
    private val idGenerator: IDGenerator
) : AccountRepository {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun createAccount(currency: CurrencyUnit, email: String): Account {
        log.debug("Creating account for email <{}> and currency <{}>...", email, currency)

        val accountNumber = idGenerator.newAccountNumber()
        val account = Account(accountNumber = accountNumber, baseCurrency = currency, email = email)
        storage[accountNumber] = account

        log.debug("Account created for email <{}> with id <{}>", email, accountNumber)
        return account
    }

    override fun getAllAccounts(): List<Account> {
        return storage.values.toList()
    }

    override fun getAccount(id: String): Account {
        return findAccount(id) ?: throw NotFoundException("Account <$id> does not exist.")
    }

    override fun findAccount(id: String): Account? {
        return storage[id]
    }

}