package com.test.api.controller.auth

import com.test.api.dto.auth.impl.AppleLoginRequestDto
import com.test.api.dto.auth.impl.GoogleLoginRequestDto
import com.test.api.dto.auth.impl.KakaoLoginRequestDto
import com.test.api.entity.Token
import com.test.api.service.auth.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.*
import org.springframework.http.*
import org.springframework.security.authentication.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "인증 Controller", description = "인증 관련 API")
class AuthController(
    private val authService: AuthService,
) {
    @Operation(summary = "구글 로그인", description = "구글 로그인")
    @PostMapping("/login/google")
    fun loginGoogle(
        @RequestBody body: GoogleLoginRequestDto,
    ): ResponseEntity<Token> {
        return ResponseEntity<Token>(authService.login(body), HttpStatus.CREATED)
    }

    @Operation(summary = "애플 로그인", description = "애플 로그인")
    @PostMapping("/login/apple")
    fun loginApple(
        @RequestBody body: AppleLoginRequestDto,
    ): ResponseEntity<Token> {
        return ResponseEntity<Token>(authService.login(body), HttpStatus.CREATED)
    }

    @Operation(summary = "카카오 로그인", description = "카카오 로그인")
    @PostMapping("/login/kakao")
    fun loginKakao(
        @RequestBody body: KakaoLoginRequestDto,
    ): ResponseEntity<Token> {
        return ResponseEntity<Token>(authService.login(body), HttpStatus.CREATED)
    }

    @Operation(summary = "엑세스 토큰 갱신", description = "엑세스 토큰 갱신")
    @Parameter(`in` = ParameterIn.HEADER, name = "Refresh-Token", description = "refresh 토큰")
    @GetMapping("/refresh")
    fun refreshAccessToken(
        request: HttpServletRequest,
    ): ResponseEntity<Token> {
        return ResponseEntity<Token>(authService.refreshAccessToken(request), HttpStatus.CREATED)
    }
}
