package com.test.api.common.util // ktlint-disable filename

import org.slf4j.LoggerFactory

inline fun <reified T> T.getLogger() = LoggerFactory.getLogger(T::class.java)!!
