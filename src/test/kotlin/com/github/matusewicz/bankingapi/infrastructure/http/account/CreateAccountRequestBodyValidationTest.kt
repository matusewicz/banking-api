package com.github.matusewicz.bankingapi.infrastructure.http.account

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.validation.Validation

class CreateAccountRequestBodyValidationTest {

    val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `should pass validation on valid input`() {
        val violations = validator.validate(CreateAccountRequestBody(email = "foo@bar.de", baseCurrency = "EUR"))
        assertThat(violations).isEmpty()
    }

    @Test
    fun `should fail validation on invalid email`() {
        val violations = validator.validate(CreateAccountRequestBody(email = "foo.de", baseCurrency = "EUR"))
        assertThat(violations).hasSize(1)
        assertThat(violations.first().invalidValue).isEqualTo("foo.de")
        assertThat(violations.first().message).isEqualTo("must be a well-formed email address")
    }

    @Test
    fun `should fail validation on invalid currency`() {
        val violations = validator.validate(CreateAccountRequestBody(email = "foo@bar.de", baseCurrency = "EURO"))
        assertThat(violations).hasSize(1)
        assertThat(violations.first().invalidValue).isEqualTo("EURO")
        assertThat(violations.first().message).isEqualTo("must be a ISO 4217 currency code")
    }
}