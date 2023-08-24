package com.test.api.component.annotaion.resolver

import com.test.api.component.annotaion.CurrentUser
import com.test.api.entity.User
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class CurrentUserResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: org.springframework.core.MethodParameter): Boolean =
        parameter.hasParameterAnnotation(CurrentUser::class.java)

    override fun resolveArgument(
        parameter: org.springframework.core.MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): User = SecurityContextHolder.getContext().authentication.principal as User
}
