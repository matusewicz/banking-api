package com.github.matusewicz.bankingapi.infrastructure.persistence

import com.github.matusewicz.bankingapi.domain.logic.exceptions.NotFoundException
import com.github.matusewicz.bankingapi.domain.model.CustomerAccount
import com.github.matusewicz.bankingapi.domain.persistence.CustomerAccountRepository
import com.github.matusewicz.bankingapi.domain.persistence.IDGenerator
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import javax.money.CurrencyUnit

@Repository
class InMemoryCustomerAccountRepository(
    @Qualifier("accountStorage") private val storage: ConcurrentHashMap<String, CustomerAccount>,
    private val idGenerator: IDGenerator
) : CustomerAccountRepository {

    private val log = LoggerFactory.getLogger(javaClass)
    private val accountLocks = ConcurrentHashMap<String, ReentrantLock>()

    override fun createAccount(currency: CurrencyUnit, email: String): CustomerAccount {
        log.debug("Creating account for email <{}> and currency <{}>...", email, currency)

        val accountNumber = idGenerator.newAccountNumber()
        val account = CustomerAccount(accountNumber = accountNumber, baseCurrency = currency, email = email)
        storage[accountNumber] = account

        log.debug("Account created for email <{}> with id <{}>", email, accountNumber)
        return account
    }

    override fun getAllAccounts(): List<CustomerAccount> {
        return storage.values.toList()
    }

    override fun getAccount(id: String): CustomerAccount {
        return findAccount(id) ?: throw NotFoundException("Account <$id> does not exist.")
    }

    override fun findAccount(id: String): CustomerAccount? {
        return storage[id]
    }

    override fun lock(account: CustomerAccount) {
        log.debug("Locking account <{}>...", account.accountNumber)
        accountLocks.getOrPut(account.accountNumber, { ReentrantLock(true) }).tryLock(5, TimeUnit.SECONDS)
    }

    override fun unlock(account: CustomerAccount) {
        log.debug("Unlocking account <{}>...", account.accountNumber)
        accountLocks[account.accountNumber]?.unlock()
    }

}