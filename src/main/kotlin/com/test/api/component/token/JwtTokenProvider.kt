package com.test.api.component.token

import com.test.api.common.util.getLogger
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.security.Key
import java.util.Date

abstract class JwtTokenProvider(private val secret: String) {
    private val logger = getLogger()

    private val signingKey: Key by lazy {
        val keyBytes: ByteArray = Decoders.BASE64.decode(secret)
        Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(id: String, expiry: Long): String = Jwts.builder()
        .setSubject(id)
        .signWith(signingKey, SignatureAlgorithm.HS512)
        .setExpiration(Date(Date().time + expiry))
        .compact()

    fun validateToken(token: String?): Boolean {
        try {
            Jwts
                .parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
            return true
        } catch (e: SecurityException) {
            logger.info("잘못된 JWT 토큰 서명")
        } catch (e: MalformedJwtException) {
            logger.info("잘못된 JWT 토큰 서명")
        } catch (e: ExpiredJwtException) {
            logger.info(e.toString())
            logger.info("만료된 JWT 토큰")
        } catch (e: UnsupportedJwtException) {
            logger.info("지원되지 않는 JWT 토큰")
        } catch (e: IllegalArgumentException) {
            logger.info("잘못된 JWT 토큰")
        } catch (e: JwtException) {
            logger.info("잘못된 JWT 토큰")
        } catch (e: Throwable) {
            logger.info("처리되지 않은 validateToken 에러")
        }
        return false
    }

    fun getUserIdByToken(token: String): String = parseClaims(signingKey, token).subject

    fun parseClaims(key: Key, token: String): Claims {
        return try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }
}
