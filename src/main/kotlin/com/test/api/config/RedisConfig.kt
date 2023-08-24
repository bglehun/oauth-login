package com.test.api.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.cluster.ClusterClientOptions
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisClusterConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import java.time.Duration
import java.util.*

@Configuration
class RedisConfig(
    @Value("\${spring.data.redis.cluster-nodes}") private val redisNodes: List<String>,
    private val objectMapper: ObjectMapper,
) {
    @Bean
    fun redisClusterConnectionFactory(): RedisConnectionFactory {
        val clusterConfiguration = RedisClusterConfiguration(redisNodes)

        // failover를 위한 topology 자동 업데이트 옵션, default 60s
        val clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
            .enableAllAdaptiveRefreshTriggers()
            .build()
        val clientOptions = ClusterClientOptions.builder()
            .topologyRefreshOptions(clusterTopologyRefreshOptions)
            .build()

        val lettuceClientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofSeconds(10))
            .clientOptions(clientOptions)
            .build()

        return LettuceConnectionFactory(clusterConfiguration, lettuceClientConfig)
    }
}
