package com.github.matusewicz.bankingapi.infrastructure.http.transfer

import com.github.matusewicz.bankingapi.TestUtils.accountAlice
import com.github.matusewicz.bankingapi.TestUtils.accountBob
import com.github.matusewicz.bankingapi.TestUtils.euro
import com.github.matusewicz.bankingapi.domain.logic.MoneyTransferService
import com.github.matusewicz.bankingapi.domain.model.MoneyTransfer
import com.github.matusewicz.bankingapi.infrastructure.JacksonConfig
import com.github.matusewicz.bankingapi.infrastructure.http.ExceptionHandling
import com.github.matusewicz.bankingapi.infrastructure.http.HttpRepresentationMapper
import com.github.matusewicz.bankingapi.infrastructure.http.LinkBuilder
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@WebMvcTest(MoneyTransferController::class)
@MockBean(MoneyTransferService::class)
@TestPropertySource(properties = ["application.base-url=http://example.org"])
@Import(LinkBuilder::class, HttpRepresentationMapper::class, JacksonConfig::class, ExceptionHandling::class)
class MoneyTransferControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val mockMoneyTransferService: MoneyTransferService
) {

    @BeforeEach
    fun setUp() {
        val moneyTransfer = mock<MoneyTransfer> {
            on { id } doReturn "0815"
        }
        whenever(mockMoneyTransferService.transfer(any(), any(), any(), any())).thenReturn(moneyTransfer)
    }

    @Test
    fun `should transfer money from one account to another -- happy path`() {
        mockMvc.perform(
            post("/transfers")
                .content(
                    """
                    {
                        "debtorAccountId": "${accountAlice.accountNumber}",
                        "creditorAccountId": "${accountBob.accountNumber}",
                        "instructedAmount": {
                            "amount": 45.50,
                            "currency": "EUR"
                        },
                        "reference": "Happy Birthday"
                    }
                    """.trimIndent()
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.header().string("Location", "http://example.org/transfers/0815"))


        verify(mockMoneyTransferService).transfer(
            debtorAccountId = eq(accountAlice.accountNumber),
            creditorAccountId = eq(accountBob.accountNumber),
            instructedAmount = eq(45.50.euro()),
            reference = eq("Happy Birthday")
        )
    }

    @Test
    fun `should validate request body`() {
        mockMvc.perform(
            post("/transfers")
                .content(
                    """
                    {
                        "debtorAccountId": "${accountAlice.accountNumber}",
                        "creditorAccountId": "${accountAlice.accountNumber}",
                        "instructedAmount": {
                            "amount": 45.50,
                            "currency": "EUR"
                        },
                        "reference": "Happy Birthday"
                    }
                    """.trimIndent()
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
                    {
                      "type": "https://zalando.github.io/problem/constraint-violation",
                      "status": 400,
                      "title": "Constraint Violation",
                      "violations": [
                        {
                          "field": "moneyTransferRequestBody",
                          "message": "debtorAccountId and creditorAccountId must not be same"
                        }
                      ]
                    }
                    """.trimIndent()
                )
            )


    }
}