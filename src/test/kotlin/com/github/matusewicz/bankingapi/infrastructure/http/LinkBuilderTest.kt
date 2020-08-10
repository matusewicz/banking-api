package com.github.matusewicz.bankingapi.infrastructure.http

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LinkBuilderTest {

    val unit = LinkBuilder(baseUrl = "https://example.org")

    @Test
    fun `should prefix relative url with base url `() {
        val absoluteUrl = unit.absoluteUrl(relative = "foobar/1234")

        assertThat(absoluteUrl).isEqualTo("https://example.org/foobar/1234")
    }

    @Test
    fun `should create absolute link to all accounts resource`() {
        assertThat(unit.accountListLink()).isEqualTo(Link("https://example.org/accounts"))
    }

    @Test
    fun `should create absolute link to one account resource`() {
        assertThat(unit.accountLink("42")).isEqualTo(Link("https://example.org/accounts/42"))
    }
}