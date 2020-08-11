package com.github.matusewicz.bankingapi.infrastructure.http.transfer

import com.github.matusewicz.bankingapi.domain.logic.MoneyTransferService
import com.github.matusewicz.bankingapi.infrastructure.http.HttpRepresentationMapper
import com.github.matusewicz.bankingapi.infrastructure.http.LinkBuilder
import com.github.matusewicz.bankingapi.infrastructure.http.transfer.MoneyTransferController.Companion.PATH
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import javax.validation.Valid

@RestController
@RequestMapping(PATH)
class MoneyTransferController(
    private val moneyTransferService: MoneyTransferService,
    private val linkBuilder: LinkBuilder,
    private val mapper: HttpRepresentationMapper
) {

    companion object {
        const val PATH = "transfers"
    }

    @PostMapping
    fun createTransfer(@Valid @RequestBody request: MoneyTransferRequestBody): ResponseEntity<Void> {
        val moneyTransfer = moneyTransferService.transfer(
            debtorAccountId = request.debtorAccountId,
            creditorAccountId = request.creditorAccountId,
            instructedAmount = request.instructedAmount,
            reference = request.reference
        )

        val location = URI.create(linkBuilder.absoluteUrl("$PATH/${moneyTransfer.id}"))
        return ResponseEntity.created(location).build()
    }

}