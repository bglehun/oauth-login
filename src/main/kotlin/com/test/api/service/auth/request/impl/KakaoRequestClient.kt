package com.test.api.service.auth.request.impl

import com.test.api.common.util.getLogger
import com.test.api.dto.auth.LoginRequestDto
import com.test.api.dto.auth.impl.KakaoLoginRequestDto
import com.test.api.entity.OauthProvider
import com.test.api.service.auth.request.OauthRequestClient
import org.springframework.beans.factory.annotation.*
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.*

@Component
class KakaoRequestClient(
    @Value("\${oauth.kakao.url}") private val url: String,
    @Value("\${oauth.validate.enable}") private val oAuthValidateEnable: Boolean,
    private val restTemplate: RestTemplate,
) : OauthRequestClient {
    val logger = getLogger()

    override val oauthProvider: OauthProvider = OauthProvider.KAKAO
    override fun validation(dto: LoginRequestDto): Boolean {
        try {
            if (!oAuthValidateEnable) return true
            val (snsId, token) = dto as KakaoLoginRequestDto
            return validateToken(snsId, token)
        } catch (e: Exception) {
            logger.info("KakaoRequestClient error = {}", e.toString())
            return false
        }
    }

    private fun validateToken(snsId: String, token: String): Boolean {
        val httpHeaders = HttpHeaders().apply {
            setBearerAuth(token)
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }

        val request = HttpEntity<Map<String, String>>(httpHeaders)

        val response = restTemplate.exchange(url, HttpMethod.GET, request, Map::class.java)
        return snsId == response.body?.get("id")
    }
}
