package slatekit.core.gate

import slatekit.common.Result

interface Gated {

    fun open(alert: Boolean)
    fun close(reason: Reason, alert: Boolean)
    fun openLater(seconds: Int, alert: Boolean)

    fun <T> attempt(call: () -> T): Result<T, GateEvent>

    fun isOpen(): Boolean
    fun isClosed(): Boolean

    fun metrics(): GateStatus
    fun states(): List<GateStatus>
}