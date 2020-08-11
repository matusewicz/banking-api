package com.github.matusewicz.bankingapi.infrastructure.http.account

import com.github.matusewicz.bankingapi.infrastructure.http.Links

data class CustomerAccountListRepresentation(val customerAccounts: List<CustomerAccountRepresentation>, val _links: Links)