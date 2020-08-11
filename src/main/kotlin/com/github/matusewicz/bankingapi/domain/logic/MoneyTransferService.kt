package com.github.matusewicz.bankingapi.domain.logic

import com.github.matusewicz.bankingapi.domain.logic.exceptions.InsufficientCreditException
import com.github.matusewicz.bankingapi.domain.model.CreditTransaction
import com.github.matusewicz.bankingapi.domain.model.DebitTransaction
import com.github.matusewicz.bankingapi.domain.model.MoneyTransfer
import com.github.matusewicz.bankingapi.domain.persistence.*
import org.springframework.stereotype.Service
import javax.money.MonetaryAmount

@Service
class MoneyTransferService(
    private val customerAccountRepository: CustomerAccountRepository,
    private val cashPointRepository: CashPointRepository,
    private val transactionRepository: TransactionRepository,
    private val transferHistoryRepository: TransferHistoryRepository,
    private val idGenerator: IDGenerator,
    private val balanceCalculator: BalanceCalculator
) {

    fun deposit(cashPointId: String, customerAccountNumber: String, amount: MonetaryAmount): MoneyTransfer {
        val cashPoint = cashPointRepository.getCashPoint(cashPointId)
        val creditorAccount = customerAccountRepository.getAccount(customerAccountNumber)
        val debitTransaction = DebitTransaction(id = newId(), account = cashPoint, amount = amount.negate())
        val creditTransaction = CreditTransaction(id = newId(), account = creditorAccount, amount = amount)

        val moneyTransfer = MoneyTransfer(
            id = newId(),
            debitTransaction = debitTransaction,
            creditTransaction = creditTransaction,
            reference = "Customer <$customerAccountNumber> deposit <$amount> at cash point <$cashPointId>"
        )

        transactionRepository.createTransactions(debitTransaction, creditTransaction)

        return moneyTransfer
    }

    // usually a transaction should be wrapped around this method to ensure all database writes are successfully committed or rolled back in case of a failure
    fun transfer(
        debtorAccountId: String,
        creditorAccountId: String,
        instructedAmount: MonetaryAmount,
        reference: String
    ): MoneyTransfer {
        val debtorAccount = customerAccountRepository.getAccount(debtorAccountId)
        val creditorAccount = customerAccountRepository.getAccount(creditorAccountId)

        val debitTransaction =
            DebitTransaction(id = newId(), account = debtorAccount, amount = instructedAmount.negate())
        val creditTransaction = CreditTransaction(id = newId(), account = creditorAccount, amount = instructedAmount)

        val moneyTransfer = MoneyTransfer(
            id = newId(),
            debitTransaction = debitTransaction,
            creditTransaction = creditTransaction,
            reference = reference
        )

        try {
            // lock account to prevent concurrent transactions exceeding account debit limit
            // TODO: think about locking creditor account as well -- in case there might be any limitations on maximum balance
            customerAccountRepository.lock(debtorAccount)

            if (balanceCalculator.calculateBalance(debtorAccountId).value.isLessThan(instructedAmount)) {
                // TODO: extract to an account debit policy to allow a configurable debit limit
                throw InsufficientCreditException("Debtor account <$debtorAccountId> has insufficient credit to transfer <$instructedAmount> to account <$creditorAccountId>.")
            }
            transactionRepository.createTransactions(debitTransaction, creditTransaction)
            transferHistoryRepository.createTransfer(moneyTransfer)

        } finally {
            customerAccountRepository.unlock(debtorAccount)
        }

        return moneyTransfer
    }

    private fun newId() = idGenerator.newTransactionId()
}