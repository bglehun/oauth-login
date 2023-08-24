package com.test.api.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI {
        val jwtSchemeName = "bearer-token"

        return OpenAPI()
            // 모든 API에 jwtSchemeName에 해당하는 Security 설정 추가
            .addSecurityItem(
                SecurityRequirement().addList(
                    jwtSchemeName,
                ),
            )
            // Authorize 설정 버튼 활성화
            .schemaRequirement(
                jwtSchemeName,
                SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                    .name(jwtSchemeName),
            )
    }
}
