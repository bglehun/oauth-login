package com.test.api.service.auth

import com.test.api.common.exception.CustomError
import com.test.api.common.exception.CustomException
import com.test.api.component.token.impl.JwtUserTokenProvider
import com.test.api.dto.auth.LoginRequestDto
import com.test.api.entity.OauthProvider
import com.test.api.entity.Token
import com.test.api.entity.User
import com.test.api.entity.UserRefreshToken
import com.test.api.repository.user.UserJpaRepository
import com.test.api.repository.user.UserRefreshTokenJpaRepository
import com.test.api.service.auth.request.OauthRequestClient
import jakarta.servlet.http.*
import org.springframework.beans.factory.annotation.*
import org.springframework.data.repository.*
import org.springframework.stereotype.Service
import java.util.function.Function
import java.util.stream.*

@Service
class AuthService(
    oAuthRequestClientInterfaceList: List<OauthRequestClient>, // interface의 구현체들을 list로 전달받음.
    private val userJpaRepository: UserJpaRepository,
    private val userRefreshTokenJpaRepository: UserRefreshTokenJpaRepository,
    private val jwtUserTokenProvider: JwtUserTokenProvider,
    @Value("\${auth.jwt.expiry}")
    private val expiry: Long,

    @Value("\${auth.jwt.refresh-expiry}")
    private val refreshExpiry: Long,
) {
    private var requestClient: Map<OauthProvider, OauthRequestClient> =
        oAuthRequestClientInterfaceList.stream()
            .collect((Collectors.toUnmodifiableMap(OauthRequestClient::oauthProvider, Function.identity())))

    fun login(dto: LoginRequestDto): Token {
        /** requestClient 선택 */
        val selectedRequestClient = requestClient[dto.oauthProvider]
            ?: throw CustomException(CustomError.NOT_SUPPORTED_PROVIDER)

        /** 입력받은 OAuth 정보로 validation */
        if (!selectedRequestClient.validation(dto)) throw CustomException(CustomError.FAILED_LOGIN_VALIDATION)

        /** snsId를 기준으로 user 조회 또는 생성 */
        val userId = findOrCreateUser(dto)

        /** jwt token 생성 */
        val token = jwtUserTokenProvider.generateToken(userId, expiry)
        val refreshToken = jwtUserTokenProvider.generateToken(userId, refreshExpiry)

        /** refresh token DB에 저장 */
        userRefreshTokenJpaRepository.save(UserRefreshToken(userId = userId, refreshToken = refreshToken))

        return Token(accessToken = token, refreshToken = refreshToken)
    }

    private fun findOrCreateUser(dto: LoginRequestDto): String = userJpaRepository.findBySnsId(dto.snsId)?.id
        ?: userJpaRepository.save(
            User(snsId = dto.snsId, snsType = dto.oauthProvider),
        ).id

    fun refreshAccessToken(request: HttpServletRequest): Token {
        val token: Token = jwtUserTokenProvider.resolveToken(request)

        if (token.refreshToken == null || !jwtUserTokenProvider.validateToken(token.refreshToken)) {
            throw CustomException(CustomError.INVALID_REFRESH_TOKEN)
        }

        val userId = jwtUserTokenProvider.getUserIdByToken(token.refreshToken)
        val userRefreshToken: String? =
            userRefreshTokenJpaRepository.findByIdOrNull(userId)?.refreshToken

        if (token.refreshToken != userRefreshToken) {
            throw CustomException(CustomError.INVALID_REFRESH_TOKEN)
        }

        return Token(accessToken = jwtUserTokenProvider.generateToken(userId, expiry), refreshToken = userRefreshToken)
    }
}
