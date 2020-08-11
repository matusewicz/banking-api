package com.github.matusewicz.bankingapi.infrastructure.http.transfer

import com.github.matusewicz.bankingapi.TestUtils.euro
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import javax.validation.Validation

class MoneyTransferRequestBodyValidationTest {

    val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `should pass validation on valid input`() {
        val violations = validator.validate(MoneyTransferRequestBody("alice", "bob", 10.euro(), "any reference"))
        assertThat(violations).isEmpty()
    }

    @Test
    fun `should fail validation when instructed amount is negative `() {
        val instructedAmount = (-1.23).euro()
        val violations = validator.validate(MoneyTransferRequestBody("alice", "bob", instructedAmount, "any reference"))
        assertThat(violations).hasSize(1)
        assertThat(violations.first().invalidValue).isEqualTo(instructedAmount)
        assertThat(violations.first().message).isEqualTo("must be greater than 0")
    }

    @Test
    fun `should fail validation when instructed amount is zero `() {
        val instructedAmount = 0.euro()
        val violations = validator.validate(MoneyTransferRequestBody("alice", "bob", instructedAmount, "any reference"))
        assertThat(violations).hasSize(1)
        assertThat(violations.first().invalidValue).isEqualTo(instructedAmount)
        assertThat(violations.first().message).isEqualTo("must be greater than 0")
    }

    @Test
    fun `should fail validation when reference is missing`() {
        val violations = validator.validate(MoneyTransferRequestBody("alice", "bob", 10.euro(), " "))
        assertThat(violations).hasSize(1)
        assertThat(violations.first().invalidValue).isEqualTo(" ")
        assertThat(violations.first().message).isEqualTo("must not be blank")
    }

    @Test
    fun `should fail validation when debtor or creditor are blank`() {
        val violations = validator.validate(MoneyTransferRequestBody("", "bob", 10.euro(), "any reference"))
        assertThat(violations).hasSize(1)
        assertThat(violations.first().invalidValue).isEqualTo("")
        assertThat(violations.first().message).isEqualTo("must not be blank")
    }
    @Test
    fun `should fail validation when debtor and creditor are same`() {
        val violations = validator.validate(MoneyTransferRequestBody("alice", "alice", 10.euro(), "any reference"))
        assertThat(violations).hasSize(1)
        assertThat(violations.first().message).isEqualTo("debtorAccountId and creditorAccountId must not be same")
    }
}