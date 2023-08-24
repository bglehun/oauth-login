package com.test.api.common.exception

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.servlet.http.HttpServletRequest
import java.text.SimpleDateFormat
import java.util.Date

@JsonInclude(JsonInclude.Include.NON_NULL)
class CustomExceptionResponse private constructor(
    val code: Int,
    val message: String,
    val path: String,
    val date: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
    val detail: String? = null,
) {
    companion object {
        fun of(customError: CustomError, request: HttpServletRequest, e: Throwable, isDebug: Boolean) =
            CustomExceptionResponse(
                code = customError.code,
                message = customError.message,
                path = request.servletPath,
                detail = if (isDebug) e.message else null,
            )
    }
}
