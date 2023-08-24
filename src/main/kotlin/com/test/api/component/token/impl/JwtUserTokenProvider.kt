package com.test.api.component.token.impl

import com.test.api.component.token.JwtTokenProvider
import com.test.api.entity.Token
import jakarta.servlet.http.HttpServletRequest
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.math.BigInteger
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.*

@Component
class JwtUserTokenProvider(
    @Value("\${auth.jwt.secret}") private val secret: String,
) : JwtTokenProvider(secret) {
    companion object {
        private const val TOKEN_PREFIX = "Bearer "
        private const val TOKEN_HEADER = "Authorization"
        private const val REFRESH_TOKEN_HEADER = "Refresh-Token"
    }

    fun resolveToken(request: HttpServletRequest): Token {
        val bearerToken = request.getHeader(TOKEN_HEADER)
        var accessToken: String? = null
        val refreshToken = request.getHeader(REFRESH_TOKEN_HEADER)
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            bearerToken.substring(TOKEN_PREFIX.length).also { accessToken = it }
        }

        return Token(accessToken, refreshToken)
    }

    fun generatePublicKey(jsonObject: JSONObject): PublicKey? {
        val nStr: String = jsonObject.getString("n")
        val eStr: String = jsonObject.getString("e")

        val nBytes = Base64.getUrlDecoder().decode(nStr)
        val eBytes = Base64.getUrlDecoder().decode(eStr)

        val n = BigInteger(1, nBytes)
        val e = BigInteger(1, eBytes)

        val publicKeySpec = RSAPublicKeySpec(n, e)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(publicKeySpec)
    }
}
