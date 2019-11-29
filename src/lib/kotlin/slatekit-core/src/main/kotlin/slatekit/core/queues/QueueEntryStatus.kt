package slatekit.core.queues

import slatekit.common.EnumLike
import slatekit.common.EnumSupport

enum class QueueEntryStatus(override val value: Int) : EnumLike {
    InActive(0),
    Processing(1),
    Completed(2),
    Discarded(3);

    companion object : EnumSupport() {

        override fun all(): Array<EnumLike> {
            return arrayOf(InActive, Processing, Completed)
        }
    }
}
