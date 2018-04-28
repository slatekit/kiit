package slatekit.core.gate

import slatekit.common.Result


interface Gated {

    fun open()
    fun close(reason:Reason)

    fun<T> attempt(call: () -> T ): Result<T, GateEvent>

    fun isOpen(): Boolean
    fun isClosed(): Boolean

    fun metrics(): GateMetrics
}