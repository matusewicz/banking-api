package com.github.matusewicz.bankingapi.infrastructure.http.account

import com.github.matusewicz.bankingapi.domain.persistence.AccountRepository
import com.github.matusewicz.bankingapi.infrastructure.http.HttpRepresentationMapper
import com.github.matusewicz.bankingapi.infrastructure.http.LinkBuilder
import com.github.matusewicz.bankingapi.infrastructure.http.account.AccountController.Companion.PATH
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.money.Monetary.getCurrency
import javax.validation.Valid

@RestController
@RequestMapping(PATH)
class AccountController(
    private val accountRepository: AccountRepository,
    private val linkBuilder: LinkBuilder,
    private val mapper: HttpRepresentationMapper
) {

    companion object {
        const val PATH = "accounts"
    }

    @PostMapping
    fun createAccount(@Valid @RequestBody request: CreateAccountRequestBody): ResponseEntity<Void> {
        val newAccount =
            accountRepository.createAccount(currency = getCurrency(request.baseCurrency), email = request.email)

        return ResponseEntity.created(URI.create(linkBuilder.absoluteUrl("$PATH/${newAccount.accountNumber}"))).build()
    }

    @GetMapping
    fun getAllAccounts(): AccountListRepresentation {
        val accounts = accountRepository.getAllAccounts()

        return mapper.map(accounts)
    }

    @GetMapping("/{accountNumber}")
    fun getAccount(@PathVariable accountNumber: String): AccountRepresentation {
        val account = accountRepository.getAccount(accountNumber)

        return mapper.map(account)
    }
}