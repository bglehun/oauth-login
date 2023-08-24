package com.test.api.dto.auth.impl

import com.test.api.dto.auth.LoginRequestDto
import com.test.api.entity.OauthProvider
import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "카카오 로그인 DTO")
data class KakaoLoginRequestDto(
    @Schema(title = "카카오 유저 식별자", example = "...")
    override val snsId: String,
    @Schema(title = "카카오 token", example = "...")
    override val token: String,
) : LoginRequestDto {
    @Schema(hidden = true)
    override val oauthProvider: OauthProvider = OauthProvider.KAKAO
}
