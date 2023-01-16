package kiit.core.queues

import kiit.common.EnumLike
import kiit.common.EnumSupport

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
