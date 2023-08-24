package com.test.api.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import javax.sql.DataSource

@Configuration
class ReplicationDataSourceConfig(
    @Value("\${spring.datasource.replication.driver-class-name}")
    private val driverClassName: String,
    @Value("\${spring.datasource.replication.username}")
    private val username: String,
    @Value("\${spring.datasource.replication.password}")
    private val password: String,
    @Value("\${spring.datasource.replication.write-url}")
    private val writeUrl: String,
    @Value("\${spring.datasource.replication.read-url}")
    private val readUrl: String,
    @Value("\${spring.datasource.hikari.maximum-pool-size}")
    private val maximumPoolSize: Int,
) {
    @Bean
    fun routingDataSource(): DataSource {
        val targetDataSource = HashMap<Any, Any>()

        val writeDataSource: DataSource = createDataSource(writeUrl)
        val readDataSource: DataSource = createDataSource(readUrl)

        targetDataSource[ReplicationRoutingDataSource.MASTER_KEY] = writeDataSource
        targetDataSource[ReplicationRoutingDataSource.REPLICA_KEY] = readDataSource

        val replicationRoutingDataSource = ReplicationRoutingDataSource()
            .apply {
                setDefaultTargetDataSource(writeDataSource)
                setTargetDataSources(targetDataSource)
                afterPropertiesSet()
            }

        return LazyConnectionDataSourceProxy(replicationRoutingDataSource)
    }

    // 주의: 해당 설정 때문에 Datasource, Hikary에 대한 application.properties로 설정한 값들은 여기서 직접 설정해줘야됨
    private fun createDataSource(url: String): DataSource {
        val hikariDataSource = HikariDataSource().also {
            it.driverClassName = driverClassName
            it.username = username
            it.password = password
            it.jdbcUrl = url
            it.maximumPoolSize = maximumPoolSize
        }

        return hikariDataSource
    }
}
