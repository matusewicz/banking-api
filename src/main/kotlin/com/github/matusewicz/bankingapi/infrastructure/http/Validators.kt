package com.github.matusewicz.bankingapi.infrastructure.http

import com.github.matusewicz.bankingapi.infrastructure.http.transfer.MoneyTransferRequestBody
import javax.money.Monetary
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(allowedTargets = [AnnotationTarget.PROPERTY_GETTER])
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [CurrencyCodeValidator::class])
annotation class CurrencyCode(
    val message: String = "must be a ISO 4217 currency code",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<in Payload>> = []
)

class CurrencyCodeValidator : ConstraintValidator<CurrencyCode, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext) = Monetary.isCurrencyAvailable(value)
}

@Target(allowedTargets = [AnnotationTarget.CLASS])
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [DebtorAndCreditorAreNotSameValidator::class])
annotation class DebtorAndCreditorAreNotSame(
    val message: String = "debtorAccountId and creditorAccountId must not be same",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<in Payload>> = []
)

class DebtorAndCreditorAreNotSameValidator : ConstraintValidator<DebtorAndCreditorAreNotSame, MoneyTransferRequestBody> {
    override fun isValid(value: MoneyTransferRequestBody, context: ConstraintValidatorContext): Boolean {
        return value.debtorAccountId != value.creditorAccountId
    }
}