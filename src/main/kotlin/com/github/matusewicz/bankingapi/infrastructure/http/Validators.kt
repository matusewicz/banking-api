package com.github.matusewicz.bankingapi.infrastructure.http

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