package slatekit.core.gate

import slatekit.common.DateTime

data class GateStatus(
    val state: GateState,
    val reason: Reason,
    val statusTimeStamp: DateTime,
    val timeStamp: DateTime,
    val currentBatch: Int,
    val processedCount: Int,
    val processedTotal: Long,
    val errorCount: Int,
    val errorTotal: Long,
    val errorLast: Exception? = null
)