package com.test.api.component.filter


import com.test.api.common.exception.CustomError
import com.test.api.common.exception.CustomException
import com.test.api.component.token.impl.JwtAdminTokenProvider
import jakarta.servlet.*
import jakarta.servlet.http.*
import org.springframework.web.filter.*

class JwtAdminAuthenticationFilter(
    private val jwtAdminTokenProvider: JwtAdminTokenProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token: String? = jwtAdminTokenProvider.resolveToken(request)

        if (token != null) {
            if (!jwtAdminTokenProvider.validateToken(token)) {
                throw CustomException(
                    CustomError.INVALID_ACCESS_TOKEN,
                )
            }
        } else {
            throw CustomException(
                CustomError.INVALID_REQUEST,
            )
        }

        filterChain.doFilter(request, response)
    }
}
