package com.github.matusewicz.bankingapi.domain.logic

import com.github.matusewicz.bankingapi.domain.persistence.CustomerAccountRepository
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.github.matusewicz.bankingapi.TestUtils
import com.github.matusewicz.bankingapi.TestUtils.euro
import com.github.matusewicz.bankingapi.domain.model.CreditTransaction
import com.github.matusewicz.bankingapi.domain.model.DebitTransaction
import com.github.matusewicz.bankingapi.domain.persistence.TransactionRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BalanceCalculatorTest {

    private val mockAccountRepository = mock<CustomerAccountRepository> {}
    private val mockTransactionRepository = mock<TransactionRepository> {}

    val accountAlice = TestUtils.accountAlice

    val unit = BalanceCalculator(mockAccountRepository, mockTransactionRepository)

    @BeforeEach
    fun setUp() {
        whenever(mockAccountRepository.getAccount(accountAlice.accountNumber)).doReturn(accountAlice)
    }

    @Test
    fun `should calculate account balance -- happy path`() {
        whenever(mockTransactionRepository.getAllTransactions(accountAlice.accountNumber)).thenReturn(
            listOf(
                CreditTransaction(id = "1", account = accountAlice, amount = 11.50.euro()),
                CreditTransaction(id = "2", account = accountAlice, amount = 0.50.euro()),
                DebitTransaction(id = "3", account = accountAlice, amount = (-2.50).euro())
            )
        )

        val balance = unit.calculateBalance(accountAlice.accountNumber)

        verify(mockAccountRepository).getAccount(accountAlice.accountNumber)
        verify(mockTransactionRepository).getAllTransactions(accountAlice.accountNumber)

        assertThat(balance.value).isEqualTo(9.50.euro())
    }

    @Test
    fun `should calculate balance of zero in base currency when account has no transactions`() {
        whenever(mockTransactionRepository.getAllTransactions(accountAlice.accountNumber)).thenReturn(emptyList())

        val balance = unit.calculateBalance(accountAlice.accountNumber)

        assertThat(balance.value).isEqualTo(0.0.euro())
    }

    @Test
    fun `should be able to calculate negative balance`() {
        whenever(mockTransactionRepository.getAllTransactions(accountAlice.accountNumber)).thenReturn(
            listOf(
                CreditTransaction(id = "1", account = accountAlice, amount = 10.00.euro()),
                CreditTransaction(id = "2", account = accountAlice, amount = 20.00.euro()),
                DebitTransaction(id = "3", account = accountAlice, amount = (-35.00).euro()),
                DebitTransaction(id = "4", account = accountAlice, amount = (-5.50).euro())
            )
        )

        val balance = unit.calculateBalance(accountAlice.accountNumber)

        assertThat(balance.value).isEqualTo((-10.50).euro())
    }
}