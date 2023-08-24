package com.test.api.common.exception

enum class CustomError(val code: Int, val message: String) {
    /** Common Error : 100 ~ 150 */
    INVALID_PARAMETER(code = 100, message = "입력하신 파라메터 정보가 올바르지 않습니다."),
    INVALID_REQUEST(code = 101, message = "잘못된 요청입니다."),

    /** Auth Error : 151 ~ 160 */
    INVALID_ACCESS_TOKEN(code = 151, message = "Access Token이 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN(code = 152, message = "Refresh Token이 올바르지 않습니다."),
    NOT_SUPPORTED_PROVIDER(code = 153, message = "지원하지 않는 로그인 타입입니다."),
    FAILED_LOGIN_VALIDATION(code = 154, message = "로그인에 필요한 oauth 인증에 실패하였습니다."),

    /** SMS Error : 201 ~  */
    SMS_PROVIDER_ERROR(
        code = 201,
        message = "SMS 서비스에 문제가 생겨 요청을 처리할 수 없습니다.",
    ),
    EXPIRED_SMS_CODE(code = 202, message = "만료된 요청입니다. 다시 시도해주세요."),
    NOT_MACHED_SMS_CODE(code = 203, message = "인증 코드가 틀렸습니다. 다시 확인해주세요."),

    /** User Error : 211 ~  */
    USER_NOT_FOUND(code = 211, message = "해당하는 유저를 찾지 못했습니다."),
    CONFLICT_NICKNAME(code = 212, message = "이미 사용중인 닉네임입니다."),
    EXPIRE_CACHED_NICKNAME(code = 213, message = "닉네임 점유 기간이 만료되었습니다."),
    NICKNAME_UPDATE_ABNORMAL_ERROR(
        code = 214,
        message = "발생할 수 없는 비정상적인 케이스에 대한 에러입니다. 상황 재현이 요구됩니다.",
    ),

    /** DB Error : 500 ~ */
    DB_ERROR(code = 500, message = "처리되지 않은 DB 에러"),
    CACHE_ERROR(code = 520, message = "처리되지 않은 Cache DB 에러"),

    /** SERVER Error 800 ~ */
    INTERNAL_SERVER_ERROR(code = 800, message = "일시적으로 문제가 발생하였습니다."),
    INTERNAL_CHAT_SERVER_ERROR(code = 801, message = "일시적으로 채팅서버에 문제가 발생하였습니다."),
    PARSING_ERROR(code = 802, message = "JSON 파싱에 실패하였습니다"),
}
