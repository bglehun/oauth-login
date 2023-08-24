package com.test.api.service.auth.request

import com.test.api.dto.auth.LoginRequestDto
import com.test.api.entity.OauthProvider

interface OauthRequestClient {
    val oauthProvider: OauthProvider
    fun validation(dto: LoginRequestDto): Boolean
}
