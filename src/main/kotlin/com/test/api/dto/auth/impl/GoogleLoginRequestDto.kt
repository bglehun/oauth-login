package com.test.api.dto.auth.impl

import com.test.api.dto.auth.LoginRequestDto
import com.test.api.entity.OauthProvider
import io.swagger.v3.oas.annotations.media.Schema

@Schema(title = "구글 로그인 DTO")
data class GoogleLoginRequestDto(

    @Schema(title = "구글 유저 식별자", example = "1456465")
    override val snsId: String,
    @Schema(title = "구글 token", example = "...")
    override val token: String,
    @Schema(title = "구글 id_token", example = "...")
    val idToken: String,
) : LoginRequestDto {
    @Schema(hidden = true)
    override val oauthProvider: OauthProvider = OauthProvider.GOOGLE
}
