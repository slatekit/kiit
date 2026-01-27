package kiit.tasks

import kiit.common.EnumLike
import kiit.common.EnumSupport


/**
 * Represents the status of an @see[Action].
 */
enum class Status(override val value: Int) : EnumLike {
    InActive  (0),
    Scheduled (1),
    Ready     (2),
    Running   (3),
    Paused    (4),
    Stopped   (5),
    Failed    (6),
    Completed (7);

    companion object : EnumSupport() {

        override fun all(): Array<EnumLike> {
            return arrayOf(InActive, Scheduled, Ready, Running, Paused, Stopped, Failed, Completed)
        }
    }
}

data class State(val status: Status, val note:String)