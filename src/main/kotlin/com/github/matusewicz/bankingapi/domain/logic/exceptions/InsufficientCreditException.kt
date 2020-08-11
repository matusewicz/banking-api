package com.github.matusewicz.bankingapi.domain.logic.exceptions

class InsufficientCreditException : RuntimeException {
    constructor(message: String) : super(message)
}