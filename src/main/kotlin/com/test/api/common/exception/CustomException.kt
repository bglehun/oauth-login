package com.test.api.common.exception

data class CustomException(
    val customError: CustomError,
) : RuntimeException() {
    override val message: String = customError.message
}
