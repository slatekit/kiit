package slatekit.core.common

import slatekit.common.ids.Paired
import slatekit.common.log.Logger

/**
 * Coordinates the work loop
 */
interface Coordinator<C> {
    val logger: Logger
    val ids: Paired

    fun sendSync(cmd:C)

    /**
     * Sends a command to manage the job/worker
     */
    suspend fun send(cmd: C)

    /**
     * Attempts to respond/handle to a command for a job/worker
     */
    suspend fun poll(): C?

    /**
     * Attempts to respond/handle to a command for a job/worker
     */
    suspend fun consume(operation: suspend (C) -> Unit)
}

