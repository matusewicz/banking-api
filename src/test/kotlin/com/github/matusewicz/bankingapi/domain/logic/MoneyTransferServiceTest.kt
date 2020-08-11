package com.github.matusewicz.bankingapi.domain.logic

import com.github.matusewicz.bankingapi.TestUtils.euro
import com.github.matusewicz.bankingapi.TestUtils.toCurrency
import com.github.matusewicz.bankingapi.domain.logic.exceptions.InsufficientCreditException
import com.github.matusewicz.bankingapi.domain.model.*
import com.github.matusewicz.bankingapi.domain.persistence.CashPointRepository
import com.github.matusewicz.bankingapi.domain.persistence.CustomerAccountRepository
import com.github.matusewicz.bankingapi.domain.persistence.TransactionRepository
import com.github.matusewicz.bankingapi.domain.persistence.TransferHistoryRepository
import com.github.matusewicz.bankingapi.infrastructure.persistence.UUIDGenerator
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MoneyTransferServiceTest {
    val mockAccountRepository = mock<CustomerAccountRepository> {}
    val mockCashPointRepository = mock<CashPointRepository> {}
    val mockTransactionRepository = mock<TransactionRepository> { }
    val mockTransferHistoryRepository = mock<TransferHistoryRepository> {}
    val mockBalanceService = mock<BalanceCalculator> { }

    val unit =
        MoneyTransferService(
            mockAccountRepository,
            mockCashPointRepository,
            mockTransactionRepository,
            mockTransferHistoryRepository,
            UUIDGenerator(),
            mockBalanceService
        )

    val accountAlice =
        CustomerAccount(accountNumber = "alice", baseCurrency = "EUR".toCurrency(), email = "alice@example.org")
    val accountBob =
        CustomerAccount(accountNumber = "bob", baseCurrency = "EUR".toCurrency(), email = "bob@example.org")

    val cashPoint = CashPoint(accountNumber = "ATM-0815")

    @BeforeEach
    fun setUp() {
        whenever(mockAccountRepository.getAccount(accountAlice.accountNumber)).thenReturn(accountAlice)
        whenever(mockAccountRepository.getAccount(accountBob.accountNumber)).thenReturn(accountBob)
        whenever(mockCashPointRepository.getCashPoint(cashPoint.accountNumber)).thenReturn(cashPoint)
        whenever(mockBalanceService.calculateBalance(accountAlice.accountNumber)).thenReturn(Balance(account = accountAlice, value = 100.euro()))
        whenever(mockBalanceService.calculateBalance(accountBob.accountNumber)).thenReturn(Balance(account = accountBob, value = 5.50.euro()))
    }

    @Test
    fun `should deposit cash at cash point -- happy path`() {
        val depositAmount = 150.00.euro()

        unit.deposit(
            cashPointId = cashPoint.accountNumber,
            customerAccountNumber = accountAlice.accountNumber,
            amount = depositAmount
        )

        val creditTxCaptor = argumentCaptor<CreditTransaction>()
        val debitTxCaptor = argumentCaptor<DebitTransaction>()

        verify(mockTransactionRepository).createTransactions(debitTxCaptor.capture(), creditTxCaptor.capture())

        val debitTx = debitTxCaptor.firstValue
        val creditTx = creditTxCaptor.firstValue

        assertThat(debitTx.account.accountNumber).isEqualTo(cashPoint.accountNumber)
        assertThat(debitTx.amount).isEqualTo(depositAmount.negate())

        assertThat(creditTx.account.accountNumber).isEqualTo(accountAlice.accountNumber)
        assertThat(creditTx.amount).isEqualTo(depositAmount)
    }

    @Test
    fun `should transfer money from one account to another -- happy path`() {
        val transferAmount = 30.50.euro()
        val reference = "happy birthday"

        unit.transfer(
            debtorAccountId = accountAlice.accountNumber,
            creditorAccountId = accountBob.accountNumber,
            instructedAmount = transferAmount,
            reference = reference
        )

        val creditTxCaptor = argumentCaptor<CreditTransaction>()
        val debitTxCaptor = argumentCaptor<DebitTransaction>()

        verify(mockAccountRepository).lock(accountAlice)
        verify(mockTransactionRepository).createTransactions(debitTxCaptor.capture(), creditTxCaptor.capture())
        verify(mockAccountRepository).unlock(accountAlice)

        val debitTx = debitTxCaptor.firstValue
        val creditTx = creditTxCaptor.firstValue

        assertThat(debitTx.account.accountNumber).isEqualTo(accountAlice.accountNumber)
        assertThat(debitTx.amount).isEqualTo(transferAmount.negate())

        assertThat(creditTx.account.accountNumber).isEqualTo(accountBob.accountNumber)
        assertThat(creditTx.amount).isEqualTo(transferAmount)
    }

    @Test
    fun `should fail money transfer on insufficient credit`() {
        val transferAmount = 30.50.euro()
        val reference = "happy birthday"

        val exception = Assertions.catchThrowable {
            unit.transfer(
                debtorAccountId = accountBob.accountNumber, // bob has only a balance of 5.50 EUR
                creditorAccountId = accountAlice.accountNumber,
                instructedAmount = transferAmount,
                reference = reference
            )
        }

        assertThat(exception).isInstanceOf(InsufficientCreditException::class.java)
    }
}