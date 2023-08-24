package com.test.api.common.exception

import com.fasterxml.jackson.databind.ObjectMapper
import com.test.api.common.util.getLogger
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.client.RestClientException

@RestControllerAdvice
class CustomExceptionHandler(
    @Value("\${logging.debug}") private val isDebug: Boolean,
    private val objectMapper: ObjectMapper,
) {
    private val logger = getLogger()

    @ExceptionHandler(CustomException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun customExceptionHandler(
        e: CustomException,
        request: HttpServletRequest,
    ): CustomExceptionResponse {
        val (customError) = e

        logger.error("CustomException - code = ${customError.code}, message =  ${customError.message}", e)

        return CustomExceptionResponse.of(customError, request, e, isDebug)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun methodArgumentNotValidExceptionHandler(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): CustomExceptionResponse {
        logger.error("methodArgumentNotValidExceptionHandler: ${e.message}", e)
        val customError = CustomError.INVALID_PARAMETER

        return CustomExceptionResponse.of(customError, request, e, isDebug)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun httpMessageNotReadableExceptionHandler(
        e: HttpMessageNotReadableException,
        request: HttpServletRequest,
    ): CustomExceptionResponse {
        logger.error("httpMessageNotReadableExceptionHandler: ${e.message}", e)

        val customError = CustomError.INVALID_PARAMETER

        return CustomExceptionResponse.of(customError, request, e, isDebug)
    }

    @ExceptionHandler(RestClientException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun chatServerExceptionHandler(
        e: Throwable,
        request: HttpServletRequest,
    ): CustomExceptionResponse {
        logger.error("chatServerExceptionHandler: ${e.message}", e)

        return try {
            // CustomExceptionResponse 형식으로 파싱이 된다면 해당 클래스를 그대로 리턴
            val message = e.message!!
            val jsonStartIndex = message.indexOf("{")
            val jsonEndIndex = message.lastIndexOf("}") + 1
            val json = message.substring(jsonStartIndex, jsonEndIndex)

            objectMapper.readValue(json, CustomExceptionResponse::class.java)
        } catch (e: Throwable) {
            // 파싱되지 않는다면 CustomException이 아닌 핸들링되지 않은 에러이기 때문에 CustomExceptionResponse를 INTERNAL 에러로 직접 생성
            // 현재는 RestTemplate을 사용하면서, 별도로 핸들링하지 않은 로직이 ChatApiService밖에 없기 때문에 해당 에러코드 사용
            val customError = CustomError.INTERNAL_CHAT_SERVER_ERROR

            CustomExceptionResponse.of(customError, request, e, isDebug)
        }
    }

    @ExceptionHandler(Throwable::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun unHandledExceptionHandler(
        e: Throwable,
        request: HttpServletRequest,
    ): CustomExceptionResponse {
        logger.error("unHandledExceptionHandler: ${e.message}", e)

        val customError = CustomError.INTERNAL_SERVER_ERROR

        return CustomExceptionResponse.of(customError, request, e, isDebug)
    }
}
