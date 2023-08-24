package com.test.api.component.annotaion

import io.swagger.v3.oas.annotations.Parameter

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@Parameter(hidden = true)
annotation class CurrentUser
