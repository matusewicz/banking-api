package com.github.matusewicz.bankingapi.infrastructure.http.account

import com.github.matusewicz.bankingapi.TestUtils
import com.github.matusewicz.bankingapi.TestUtils.euro
import com.github.matusewicz.bankingapi.domain.logic.MoneyTransferService
import com.github.matusewicz.bankingapi.domain.logic.exceptions.NotFoundException
import com.github.matusewicz.bankingapi.domain.model.CustomerAccount
import com.github.matusewicz.bankingapi.domain.model.MoneyTransfer
import com.github.matusewicz.bankingapi.domain.persistence.CustomerAccountRepository
import com.github.matusewicz.bankingapi.infrastructure.JacksonConfig
import com.github.matusewicz.bankingapi.infrastructure.http.ExceptionHandling
import com.github.matusewicz.bankingapi.infrastructure.http.HttpRepresentationMapper
import com.github.matusewicz.bankingapi.infrastructure.http.LinkBuilder
import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import javax.money.Monetary

@WebMvcTest(AccountController::class)
@MockBean(CustomerAccountRepository::class, MoneyTransferService::class)
@TestPropertySource(properties = ["application.base-url=http://example.org"])
@Import(LinkBuilder::class, HttpRepresentationMapper::class, JacksonConfig::class, ExceptionHandling::class)
class AccountControllerTest(
    @Autowired val mockMvc: MockMvc,
    @Autowired val mockCustomerAccountRepository: CustomerAccountRepository,
    @Autowired val mockMoneyTransferService: MoneyTransferService
) {

    companion object {
        val EUR = Monetary.getCurrency("EUR")
    }

    @Test
    fun `should create new account and return local header -- happy path`() {
        whenever(mockCustomerAccountRepository.createAccount(any(), any())).thenReturn(
            CustomerAccount(
                accountNumber = "0815",
                email = "any@example.org",
                baseCurrency = Monetary.getCurrency("EUR")
            )
        )
        mockMvc.perform(
            post("/accounts")
                .content("""{"email":"foobar@example.org", "baseCurrency":"EUR"}""")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "http://example.org/accounts/0815"))
    }

    @Test
    fun `should fail account creation on invalid email`() {
        mockMvc.perform(
            post("/accounts")
                .content("""{"email":"invalid-email-address", "baseCurrency":"EUR"}""")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().is4xxClientError)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(content().json("""{"title":"Constraint Violation", "violations":[{"field":"email"}]}"""))
    }

    @Test
    fun `should fail account creation on invalid currency code`() {
        mockMvc.perform(
            post("/accounts")
                .content("""{"email":"foobar@example.org", "baseCurrency":"EURO"}""")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().is4xxClientError)
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(content().json("""{"title":"Constraint Violation", "violations":[{"field":"baseCurrency"}]}"""))
    }

    @Test
    fun `should retrieve all accounts`() {
        whenever(mockCustomerAccountRepository.getAllAccounts()).thenReturn(
            listOf(
                CustomerAccount("alice", "alice@example.org", EUR),
                CustomerAccount("bob", "bob@example.org", EUR)
            )
        )

        mockMvc.perform(get("/accounts"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
                    {
                      "customerAccounts": [
                        {
                          "accountNumber": "alice",
                          "email": "alice@example.org",
                          "baseCurrency": "EUR",
                          "_links": {
                            "_self": {
                              "href": "http://example.org/accounts/alice"
                            }
                          }
                        },
                        {
                          "accountNumber": "bob",
                          "email": "bob@example.org",
                          "baseCurrency": "EUR",
                          "_links": {
                            "_self": {
                              "href": "http://example.org/accounts/bob"
                            }
                          }
                        }
                      ],
                      "_links": {
                        "_self": {
                          "href": "http://example.org/accounts"
                        }
                      }
                    }
                    """.trimIndent()
                )
            )
    }

    @Test
    fun `should retrieve account by id`() {
        whenever(mockCustomerAccountRepository.getAccount("alice")).thenReturn(
            CustomerAccount(
                accountNumber = "alice",
                email = "alice@example.org",
                baseCurrency = Monetary.getCurrency("EUR")
            )
        )

        mockMvc.perform(get("/accounts/alice"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
                    {
                      "accountNumber": "alice",
                      "email": "alice@example.org",
                      "baseCurrency": "EUR",
                      "_links": {
                        "_self": {
                          "href": "http://example.org/accounts/alice"
                        },
                        "accounts": {
                          "href": "http://example.org/accounts"
                        }
                      }
                    }
                    """.trimIndent()
                )
            )
    }

    @Test
    fun `should deposit money to customer account`() {
        val moneyTransfer = mock<MoneyTransfer> {
            on { id } doReturn "0815"
        }
        whenever(mockMoneyTransferService.deposit(any(), any(), any())).thenReturn(moneyTransfer)

        mockMvc.perform(
            post("/accounts/alice/deposit")
                .content(
                    """
                    {
                        "cashPointId": "CP-01",
                        "depositAmount": {
                            "amount": 45.50,
                            "currency": "EUR"
                        }
                    }
                    """.trimIndent()
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isCreated)
            .andExpect(header().string("Location", "http://example.org/transfers/0815"))

        verify(mockMoneyTransferService).deposit(
            cashPointId = eq("CP-01"),
            customerAccountNumber = eq(TestUtils.accountAlice.accountNumber),
            amount = eq(45.50.euro())
        )
    }

    @Test
    fun `should return 404 when resource not found`() {
        whenever(mockCustomerAccountRepository.getAccount("unknown")).thenThrow(NotFoundException::class.java)

        mockMvc.perform(get("/accounts/unknown"))
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().is4xxClientError)
            .andExpect(content().json("""{"title":"Resource not found","status":404}"""))
    }
}