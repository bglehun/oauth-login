package com.test.api.component.filter

import com.test.api.common.exception.CustomError
import com.test.api.common.exception.CustomException
import com.test.api.component.token.impl.JwtUserTokenProvider
import com.test.api.entity.Token
import com.test.api.repository.user.UserJpaRepository
import jakarta.servlet.*
import jakarta.servlet.http.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.authentication.*
import org.springframework.security.core.context.*
import org.springframework.web.filter.*

class JwtUserAuthenticationFilter(
    private val jwtUserTokenProvider: JwtUserTokenProvider,
    private val userJpaRepository: UserJpaRepository,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token: Token = jwtUserTokenProvider.resolveToken(request)

        if (token.accessToken != null) {
            if (!jwtUserTokenProvider.validateToken(token.accessToken)) {
                throw CustomException(
                    CustomError.INVALID_ACCESS_TOKEN,
                )
            }
            val userId = jwtUserTokenProvider.getUserIdByToken(token.accessToken)

            val user =
                userJpaRepository.findByIdOrNull(userId) ?: throw CustomException(CustomError.INVALID_ACCESS_TOKEN)

            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(user, null)
        } else {
            logger.debug("access token is empty. ${request.requestURI}")
        }

        filterChain.doFilter(request, response)
    }
}
