package com.github.matusewicz.bankingapi.infrastructure.http.balance

import com.github.matusewicz.bankingapi.domain.logic.BalanceCalculator
import com.github.matusewicz.bankingapi.infrastructure.http.HttpRepresentationMapper
import com.github.matusewicz.bankingapi.infrastructure.http.account.AccountController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/${AccountController.PATH}/{accountNumber}/${BalanceController.PATH}")
class BalanceController(
    private val balanceCalculator: BalanceCalculator,
    private val mapper: HttpRepresentationMapper
) {

    companion object {
        const val PATH = "balance"
    }

    @GetMapping
    fun getBalance(@PathVariable accountNumber: String): BalanceRepresentation {
        val balance = balanceCalculator.calculateBalance(accountNumber)

        return mapper.map(balance)
    }
}