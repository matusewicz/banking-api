package com.github.matusewicz.bankingapi.infrastructure.http.account

import com.github.matusewicz.bankingapi.domain.logic.exceptions.NotFoundException
import com.github.matusewicz.bankingapi.domain.model.Account
import com.github.matusewicz.bankingapi.domain.persistence.AccountRepository
import com.github.matusewicz.bankingapi.infrastructure.JacksonConfig
import com.github.matusewicz.bankingapi.infrastructure.http.ExceptionHandling
import com.github.matusewicz.bankingapi.infrastructure.http.HttpRepresentationMapper
import com.github.matusewicz.bankingapi.infrastructure.http.LinkBuilder
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.junit.jupiter.api.BeforeEach
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
@MockBean(AccountRepository::class)
@TestPropertySource(properties = ["application.base-url=http://example.org"])
@Import(LinkBuilder::class, HttpRepresentationMapper::class, JacksonConfig::class, ExceptionHandling::class)
class AccountControllerTest(@Autowired val mockMvc: MockMvc, @Autowired val accountRepository: AccountRepository) {

    companion object {
        val EUR = Monetary.getCurrency("EUR")
    }

    @BeforeEach
    fun setUp() {

    }

    @Test
    fun `should create new account and return local header -- happy path`() {
        whenever(accountRepository.createAccount(any(), any())).thenReturn(
            Account(
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
        whenever(accountRepository.getAllAccounts()).thenReturn(
            listOf(
                Account("alice", "alice@example.org", EUR),
                Account("bob", "bob@example.org", EUR)
            )
        )

        mockMvc.perform(get("/accounts"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(
                content().json(
                    """
                    {
                      "accounts": [
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
        whenever(accountRepository.getAccount("alice")).thenReturn(
            Account(
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
    fun `should return 404 when resource not found`() {
        whenever(accountRepository.getAccount("unknown")).thenThrow(NotFoundException::class.java)

        mockMvc.perform(get("/accounts/unknown"))
            .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(status().is4xxClientError)
            .andExpect(
                content().json(
                    """
                    {
                      "title": "Resource not found",
                      "status": 404
                    }
                    """.trimIndent()
                )
            )
    }
}