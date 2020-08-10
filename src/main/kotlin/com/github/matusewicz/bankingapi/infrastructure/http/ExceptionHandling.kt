package com.github.matusewicz.bankingapi.infrastructure.http

import com.github.matusewicz.bankingapi.domain.logic.exceptions.NotFoundException
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.NativeWebRequest
import org.zalando.problem.Problem
import org.zalando.problem.Status
import org.zalando.problem.spring.web.advice.ProblemHandling

/**
 * Configures all known [ExceptionHandler]s and enables `problem+json` support.
 *
 * As a result, `ErrorMvcAutoConfiguration` is
 * [disabled](https://github.com/zalando/problem-spring-web/tree/master/problem-spring-web#configuration).
 */
@ControllerAdvice
@EnableAutoConfiguration(exclude = [ErrorMvcAutoConfiguration::class])
class ExceptionHandling : ProblemHandling {

    @ExceptionHandler
    override fun handleThrowable(exception: Throwable, request: NativeWebRequest): ResponseEntity<Problem> = create(
        exception, Problem.builder()
            .withStatus(Status.INTERNAL_SERVER_ERROR)
            .withTitle("Unknown exception")
            .withDetail("Sorry, something unexpected went wrong. Please contact us.")
            .build(), request
    )

    @ExceptionHandler
    fun handleNotFound(exception: NotFoundException, request: NativeWebRequest): ResponseEntity<Problem> = create(
        exception, Problem.builder()
            .withStatus(Status.NOT_FOUND)
            .withTitle("Resource not found")
            .withDetail(exception.message)
            .build(), request
    )
}
