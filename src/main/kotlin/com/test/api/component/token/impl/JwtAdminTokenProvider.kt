package com.test.api.component.token.impl

import com.test.api.component.token.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.util.*

@Component
class JwtAdminTokenProvider(
    @Value("\${auth.jwt.admin-secret}") private val adminSecret: String,
) : JwtTokenProvider(adminSecret) {
    companion object {
        private const val TOKEN_PREFIX = "Bearer "
        private const val TOKEN_HEADER = "Authorization"
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(TOKEN_HEADER)
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            bearerToken.substring(TOKEN_PREFIX.length)
        } else {
            null
        }
    }
}
