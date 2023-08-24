package com.test.api.component.filter


import com.fasterxml.jackson.databind.ObjectMapper
import com.test.api.common.exception.CustomError
import com.test.api.common.exception.CustomException
import com.test.api.common.exception.CustomExceptionResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationExceptionFilter(
    private val objectMapper: ObjectMapper,
    @Value("\${logging.debug}") private val isDebug: Boolean,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) = try {
        filterChain.doFilter(request, response)
    } catch (e: Exception) {
        val customError =
            if (e is CustomException) e.customError else CustomError.INTERNAL_SERVER_ERROR

        logger.error(this.javaClass.simpleName, e)

        response.status = HttpStatus.BAD_REQUEST.value()
        response.addHeader("Content-Type", "application/json; charset=UTF-8")

        response.writer.write(
            objectMapper.writeValueAsString(
                /* value = */ CustomExceptionResponse.of(customError, request, e, isDebug),
            ),
        )
    }
}
