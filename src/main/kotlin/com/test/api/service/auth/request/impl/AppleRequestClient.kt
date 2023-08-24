package com.test.api.service.auth.request.impl

import com.test.api.common.util.getLogger
import com.test.api.component.token.impl.JwtUserTokenProvider
import com.test.api.dto.auth.LoginRequestDto
import com.test.api.dto.auth.impl.AppleLoginRequestDto
import com.test.api.entity.OauthProvider
import com.test.api.service.auth.request.OauthRequestClient
import org.json.JSONObject
import org.springframework.beans.factory.annotation.*
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.*
import java.net.URI
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

@Component
class AppleRequestClient(
    @Value("\${oauth.apple.keysUrl}") private val url: String,
    @Value("\${oauth.validate.enable}") private val oAuthValidateEnable: Boolean,
    private val jwtUserTokenProvider: JwtUserTokenProvider,
    private val restTemplate: RestTemplate,
) : OauthRequestClient {
    val logger = getLogger()
    override val oauthProvider: OauthProvider = OauthProvider.APPLE

    override fun validation(dto: LoginRequestDto): Boolean {
        try {
            if (!oAuthValidateEnable) return true
            val (snsId, token) = dto as AppleLoginRequestDto

            return validateToken(snsId, token)
        } catch (e: Exception) {
            logger.info("AppleRequestClient error = {}", e.toString())
            return false
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getPublicKeysRequest(): List<JSONObject> {
        val response = restTemplate.getForEntity(URI(url), Map::class.java)
        return (response.body?.get("keys") as ArrayList<LinkedHashMap<String, String>>).map { JSONObject(it) }
    }

    private fun validateToken(snsId: String, idToken: String): Boolean {
        /** get public key list from apple */
        val publicKeys = getPublicKeysRequest()

        /** jwt header decode */
        val headerBase64 = idToken.split(".")[0]
        val headerBytes = Base64.getUrlDecoder().decode(headerBase64)
        val header = JSONObject(String(headerBytes, StandardCharsets.UTF_8))

        /** select matched public key*/
        val idTokenAlg = header["alg"]
        val idTokenKid = header.getString("kid")

        val selectedApplePublicKey = publicKeys.firstOrNull {
            it["alg"] == idTokenAlg && it["kid"] == idTokenKid
        }

        /** generated new public key */
        if (selectedApplePublicKey != null) {
            val generatedPublicKey = jwtUserTokenProvider.generatePublicKey(selectedApplePublicKey) as Key
            val claims = jwtUserTokenProvider.parseClaims(generatedPublicKey, idToken)
            return snsId == claims["sub"]
        }
        return false
    }
}
