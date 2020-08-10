package com.github.matusewicz.bankingapi.infrastructure.http.account

import com.github.matusewicz.bankingapi.infrastructure.http.Links

data class AccountListRepresentation(val accounts: List<AccountRepresentation>, val _links: Links)