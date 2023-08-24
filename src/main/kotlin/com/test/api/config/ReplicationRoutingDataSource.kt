package com.test.api.config

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager

class ReplicationRoutingDataSource : AbstractRoutingDataSource() {
    override fun determineCurrentLookupKey(): String {
        val isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly()

        if (isReadOnly) {
            logger.debug("Connection Replica")
            return REPLICA_KEY
        }

        logger.debug("Connection Master")
        return MASTER_KEY
    }

    companion object {
        const val MASTER_KEY = "master"
        const val REPLICA_KEY = "replica"
    }
}
