package com.test.api.service.auth.request.impl

import com.test.api.common.util.getLogger
import com.test.api.dto.auth.LoginRequestDto
import com.test.api.dto.auth.impl.GoogleLoginRequestDto
import com.test.api.entity.OauthProvider
import com.test.api.service.auth.request.OauthRequestClient
import org.springframework.beans.factory.annotation.*
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.*

@Component
class GoogleRequestClient(
    @Value("\${oauth.google.url}") private val url: String,
    @Value("\${oauth.validate.enable}") private val oAuthValidateEnable: Boolean,
    private val restTemplate: RestTemplate,
) : OauthRequestClient {
    val logger = getLogger()
    override val oauthProvider: OauthProvider = OauthProvider.GOOGLE

    override fun validation(dto: LoginRequestDto): Boolean {
        try {
            if (!oAuthValidateEnable) return true
            val (snsId, token, idToken) = dto as GoogleLoginRequestDto

            return validateToken(snsId, token) && validateIdToken(snsId, idToken)
        } catch (e: Exception) {
            logger.info("GoogleRequestClient error = {}", e.toString())
            return false
        }
    }

    private fun validateToken(snsId: String, token: String): Boolean {
        val url = UriComponentsBuilder.fromUriString(url)
            .queryParam("access_token", token)
            .build().toUriString()

        val response = restTemplate.getForEntity(url, Map::class.java)
        return snsId == response.body?.get("sub")
    }

    private fun validateIdToken(snsId: String, idToken: String): Boolean {
        val url = UriComponentsBuilder.fromUriString(url)
            .queryParam("id_token", idToken)
            .build().toUriString()

        val response = restTemplate.getForEntity(url, Map::class.java)
        return snsId == response.body?.get("sub")
    }
}
