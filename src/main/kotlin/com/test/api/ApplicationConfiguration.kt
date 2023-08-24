package com.test.api

import com.test.api.common.util.getLogger
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class ApplicationConfiguration {
    private val logger = getLogger()

    @Bean
    fun initializer(environment: Environment) = ApplicationRunner {
        logger.info("Active profiles : ${environment.activeProfiles.toList()}")
    }
}
