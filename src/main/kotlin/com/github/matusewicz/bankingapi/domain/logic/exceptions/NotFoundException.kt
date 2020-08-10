package com.github.matusewicz.bankingapi.domain.logic.exceptions

class NotFoundException : RuntimeException {
    constructor(message: String) : super(message)
}