package com.github.matusewicz.bankingapi.infrastructure.http.transaction

import com.github.matusewicz.bankingapi.TestUtils.accountAlice
import com.github.matusewicz.bankingapi.TestUtils.euro
import com.github.matusewicz.bankingapi.domain.logic.exceptions.NotFoundException
import com.github.matusewicz.bankingapi.domain.model.CreditTransaction
import com.github.matusewicz.bankingapi.domain.model.DebitTransaction
import com.github.matusewicz.bankingapi.domain.persistence.TransactionRepository
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TransactionController::class)
@MockBean(TransactionRepository::class)
@TestPropertySource(properties = ["application.base-url=http://example.org"])
@Import(LinkBuilder::class, HttpRepresentationMapper::class, JacksonConfig::class, ExceptionHandling::class)
class TransactionControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val transactionRepository: TransactionRepository
) {

    @Test
    fun `should retrieve all transactions for account`() {
        val transactions = listOf(
            CreditTransaction("first", accountAlice, 10.00.euro()),
            CreditTransaction("second", accountAlice, 50.00.euro()),
            DebitTransaction("third", accountAlice, 5.00.euro())
        )

        whenever(transactionRepository.getAllTransactions(accountAlice.accountNumber)).thenReturn(transactions)

        mockMvc.perform(get("/accounts/${accountAlice.accountNumber}/transactions"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
                    {
                      "transactions": [
                        {
                          "id": "first",
                          "transactionAmount": {
                            "amount": 10,
                            "currency": "EUR"
                          },
                          "_links": {
                            "_self": {
                              "href": "http://example.org/accounts/alice/transactions/first"
                            }
                          }
                        },
                        {
                          "id": "second",
                          "transactionAmount": {
                            "amount": 50,
                            "currency": "EUR"
                          },
                          "_links": {
                            "_self": {
                              "href": "http://example.org/accounts/alice/transactions/second"
                            }
                          }
                        },
                        {
                          "id": "third",
                          "transactionAmount": {
                            "amount": 5,
                            "currency": "EUR"
                          },
                          "_links": {
                            "_self": {
                              "href": "http://example.org/accounts/alice/transactions/third"
                            }
                          }
                        }
                      ],
                      "_links": {
                        "_self": {
                          "href": "http://example.org/accounts/alice/transactions"
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
    fun `should retrieve transaction by account and id`() {
        val transactionId = "any-tx-id"

        whenever(transactionRepository.getTransaction(accountAlice.accountNumber, transactionId))
            .thenReturn(CreditTransaction(transactionId, accountAlice, 50.00.euro()))

        mockMvc.perform(get("/accounts/${accountAlice.accountNumber}/transactions/$transactionId"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
                    {
                      "id": "any-tx-id",
                      "transactionAmount": {
                        "amount": 50,
                        "currency": "EUR"
                      },
                      "_links": {
                        "_self": {
                          "href": "http://example.org/accounts/alice/transactions/any-tx-id"
                        },
                        "transactions": {
                          "href": "http://example.org/accounts/alice/transactions"
                        }
                      }
                    }
                    """.trimIndent()
                )
            )
    }

    @Test
    fun `should return 404 when resource not found`() {
        whenever(transactionRepository.getTransaction(any(), any())).thenThrow(NotFoundException::class.java)

        mockMvc.perform(get("/accounts/unknown-account/transactions/unknown-tx"))
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().is4xxClientError)
            .andExpect(content().json("""{"title":"Resource not found","status":404}"""))

    }
}