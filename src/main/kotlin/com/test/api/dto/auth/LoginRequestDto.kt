package com.test.api.dto.auth

import com.test.api.entity.OauthProvider

interface LoginRequestDto {
    val snsId: String
    val token: String
    val oauthProvider: OauthProvider
}
