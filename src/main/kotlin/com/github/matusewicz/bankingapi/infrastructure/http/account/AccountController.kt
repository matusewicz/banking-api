package com.github.matusewicz.bankingapi.infrastructure.http.account

import com.github.matusewicz.bankingapi.domain.logic.MoneyTransferService
import com.github.matusewicz.bankingapi.domain.persistence.CustomerAccountRepository
import com.github.matusewicz.bankingapi.infrastructure.http.HttpRepresentationMapper
import com.github.matusewicz.bankingapi.infrastructure.http.LinkBuilder
import com.github.matusewicz.bankingapi.infrastructure.http.account.AccountController.Companion.PATH
import com.github.matusewicz.bankingapi.infrastructure.http.transfer.MoneyTransferController
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.money.Monetary.getCurrency
import javax.validation.Valid

@RestController
@RequestMapping(PATH)
class AccountController(
    private val customerAccountRepository: CustomerAccountRepository,
    private val transferService: MoneyTransferService,
    private val linkBuilder: LinkBuilder,
    private val mapper: HttpRepresentationMapper
) {

    companion object {
        const val PATH = "accounts"
    }

    @PostMapping
    fun createAccount(@Valid @RequestBody request: CreateAccountRequestBody): ResponseEntity<Void> {
        val newAccount =
            customerAccountRepository.createAccount(currency = getCurrency(request.baseCurrency), email = request.email)

        return ResponseEntity.created(URI.create(linkBuilder.absoluteUrl("$PATH/${newAccount.accountNumber}"))).build()
    }

    @GetMapping
    fun getAllAccounts(): CustomerAccountListRepresentation {
        val accounts = customerAccountRepository.getAllAccounts()

        return mapper.map(accounts)
    }

    @GetMapping("/{accountNumber}")
    fun getAccount(@PathVariable accountNumber: String): CustomerAccountRepresentation {
        val account = customerAccountRepository.getAccount(accountNumber)

        return mapper.map(account)
    }

    @PostMapping("/{accountNumber}/deposit")
    fun depositMoney(
        @PathVariable accountNumber: String,
        @Valid @RequestBody request: DepositMoneyRequestBody
    ): ResponseEntity<Void> {
        val deposit = transferService.deposit(
            cashPointId = request.cashPointId,
            customerAccountNumber = accountNumber,
            amount = request.depositAmount
        )

        val location = URI.create(linkBuilder.absoluteUrl("${MoneyTransferController.PATH}/${deposit.id}"))

        return ResponseEntity.created(location).build()
    }
}