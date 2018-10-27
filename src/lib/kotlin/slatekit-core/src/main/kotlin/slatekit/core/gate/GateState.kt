package slatekit.core.gate

interface GateState

object Open : GateState
object Closed : GateState

interface Reason

object NotApplicable : Reason
object ErrorsHigh : Reason
object VolumeHigh : Reason
object Maintainance : Reason
data class ManualClose(val description: String) : Reason