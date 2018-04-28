package slatekit.core.gate

data class GateEvent(val name:String,
                     val state: GateState,
                     val reason: Reason,
                     val metrics: GateMetrics)