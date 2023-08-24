package com.test.api.config

import com.test.api.component.annotaion.resolver.CurrentUserResolver
import com.test.api.component.filter.JwtAdminAuthenticationFilter
import com.test.api.component.filter.JwtAuthenticationExceptionFilter
import com.test.api.component.filter.JwtUserAuthenticationFilter
import com.test.api.component.token.impl.JwtAdminTokenProvider
import com.test.api.component.token.impl.JwtUserTokenProvider
import com.test.api.repository.user.UserJpaRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AndRequestMatcher
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@EnableWebSecurity
class WebSecurityConfig(
    private val jwtUserTokenProvider: JwtUserTokenProvider,
    private val jwtAdminTokenProvider: JwtAdminTokenProvider,
    private val userJpaRepository: UserJpaRepository,
    private val jwtAuthenticationExceptionFilter: JwtAuthenticationExceptionFilter,

    ) : WebMvcConfigurer {

    fun defaultHttpConfigure(http: HttpSecurity) {
        http
            .cors { it.disable() }
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }
        http.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

        http.addFilterBefore(
            jwtAuthenticationExceptionFilter,
            UsernamePasswordAuthenticationFilter::class.java,
        )
    }

    @Bean
    @Order(0)
    fun adminFilterChain(http: HttpSecurity): SecurityFilterChain {
        defaultHttpConfigure(http)

        http.securityMatcher("/api/v1/admin/**")

        http.addFilterBefore(
            JwtAdminAuthenticationFilter(jwtAdminTokenProvider),
            UsernamePasswordAuthenticationFilter::class.java,
        )
        return http.build()
    }

    @Bean
    @Order(1)
    fun userFilterChain(http: HttpSecurity): SecurityFilterChain {
        defaultHttpConfigure(http)

        http.securityMatcher(
            AndRequestMatcher(
                AntPathRequestMatcher("/api/v1/**"),
                NegatedRequestMatcher(AntPathRequestMatcher("/api/v1/auth/**")),
            ),
        )

        http.addFilterBefore(
            JwtUserAuthenticationFilter(jwtUserTokenProvider, userJpaRepository),
            UsernamePasswordAuthenticationFilter::class.java,
        )

        return http.build()
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(CurrentUserResolver())
    }
}
