package com.github.matusewicz.bankingapi.infrastructure.http

typealias Links = Map<String, Link>

data class Link(val href: String)