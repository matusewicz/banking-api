package com.github.matusewicz.bankingapi.infrastructure.http.transfer

import com.github.matusewicz.bankingapi.infrastructure.http.Links

data class MoneyTransferListRepresentation(val transfers: List<MoneyTransferRepresentation>, val _links: Links?)