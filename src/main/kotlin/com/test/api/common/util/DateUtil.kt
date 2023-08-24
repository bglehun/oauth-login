package com.test.api.common.util

import java.time.*

object DateUtil {
    fun getCurrentTimestamp(): Long {
        val instant = Instant.now()
        return instant.toEpochMilli() * 1_000_000 + instant.nano.toLong()
    }
}
