package com.github.matusewicz.bankingapi.infrastructure.http.balance

import com.github.matusewicz.bankingapi.TestUtils
import com.github.matusewicz.bankingapi.TestUtils.euro
import com.github.matusewicz.bankingapi.domain.logic.BalanceCalculator
import com.github.matusewicz.bankingapi.domain.logic.exceptions.NotFoundException
import com.github.matusewicz.bankingapi.domain.model.Balance
import com.github.matusewicz.bankingapi.infrastructure.JacksonConfig
import com.github.matusewicz.bankingapi.infrastructure.http.ExceptionHandling
import com.github.matusewicz.bankingapi.infrastructure.http.HttpRepresentationMapper
import com.github.matusewicz.bankingapi.infrastructure.http.LinkBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@WebMvcTest(BalanceController::class)
@MockBean(BalanceCalculator::class)
@TestPropertySource(properties = ["application.base-url=http://example.org"])
@Import(LinkBuilder::class, HttpRepresentationMapper::class, JacksonConfig::class, ExceptionHandling::class)
class BalanceControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val mockBalanceCalculator: BalanceCalculator
) {

    @Test
    fun `should calculate account balance`() {
        whenever(mockBalanceCalculator.calculateBalance(any())).thenReturn(
            Balance(TestUtils.accountAlice, 150.50.euro())
        )

        mockMvc.perform(get("/accounts/alice/balance"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
                    {
                      "accountNumber": "alice",
                      "balance": {
                        "amount": 150.5,
                        "currency": "EUR"
                      },
                      "_links": {
                        "_self": {
                          "href": "http://example.org/accounts/alice/balance"
                        },
                        "account": {
                          "href": "http://example.org/accounts/alice"
                        }
                      }
                    }
                    """.trimIndent()
                )
            )
    }

    @Test
    fun `should return 404 when resource not found`() {
        whenever(mockBalanceCalculator.calculateBalance(any())).thenThrow(NotFoundException::class.java)

        mockMvc.perform(get("/accounts/alice/balance"))
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
            .andExpect(MockMvcResultMatchers.content().json("""{"title":"Resource not found","status":404}"""))
    }
}