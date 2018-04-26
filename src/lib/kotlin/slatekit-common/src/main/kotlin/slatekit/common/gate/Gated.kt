package slatekit.common.gate

import slatekit.common.Result


interface Gated {

    fun open()
    fun close()

    fun<T> attempt(call: () -> T ): Result<T, String>

    fun isOpen(): Boolean
    fun isClosed(): Boolean

    fun status(): GateMetrics
}